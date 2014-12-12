package org.nutz.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.nutz.http.sender.GetSender;
import org.nutz.http.sender.PostSender;
import org.nutz.lang.stream.NullInputStream;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public abstract class Sender {

    /**
     * 默认连接超时, 30秒
     */
    public static int Default_Conn_Timeout = 30 * 1000;
    /**
     * 默认读取超时, 10分钟
     */
    public static int Default_Read_Timeout = 10 * 60 * 1000;

    private static final Log log = Logs.get();

    public static Sender create(String url) {
        return create(Request.get(url));
    }

    public static Sender create(String url, int timeout) {
        return create(Request.get(url)).setTimeout(timeout);
    }

    public static Sender create(Request request) {
        return request.isGet() || request.isDelete() ? new GetSender(request)
                                                    : new PostSender(request);
    }

    public static Sender create(Request request, int timeout) {
        Sender sender = request.isGet() || request.isDelete() ? new GetSender(request)
                                                             : new PostSender(request);
        return sender.setTimeout(timeout);
    }

    protected Request request;

    protected int timeout;

    protected HttpURLConnection conn;

    protected Sender(Request request) {
        this.request = request;
    }

    public abstract Response send() throws HttpException;

    protected Response createResponse(Map<String, String> reHeaders) throws IOException {
        Response rep = null;
        if (reHeaders != null) {
            rep = new Response(conn, reHeaders);
            if (rep.isOK()) {
                InputStream is1 = conn.getInputStream();
                InputStream is2 = null;
                String encoding = conn.getContentEncoding();
                // 如果采用了压缩,则需要处理否则都是乱码
                if (encoding != null && encoding.contains("gzip")) {
                    is2 = new GZIPInputStream(is1);
                } else if (encoding != null && encoding.contains("deflate")) {
                    is2 = new InflaterInputStream(is1, new Inflater(true));
                } else {
                    is2 = is1;
                }

                BufferedInputStream is = new BufferedInputStream(is2);
                rep.setStream(is);
            }

            else {
                try {
                    rep.setStream(conn.getInputStream());
                }
                catch (IOException e) {
                    rep.setStream(new NullInputStream());
                }
            }
        }
        return rep;
    }

    protected Map<String, String> getResponseHeader() throws IOException {
        if (conn.getResponseCode() < 0)
            throw new IOException("Network error!! resp code="+conn.getResponseCode());
        Map<String, String> reHeaders = new HashMap<String, String>();
        for (Entry<String, List<String>> en : conn.getHeaderFields().entrySet()) {
            List<String> val = en.getValue();
            if (null != val && val.size() > 0)
                reHeaders.put(en.getKey(), en.getValue().get(0));
        }
        return reHeaders;
    }

    protected void setupDoInputOutputFlag() {
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // conn.setUseCaches(false);
    }

    protected void openConnection() throws IOException {
        ProxySwitcher proxySwitcher = Http.proxySwitcher;
        if (proxySwitcher != null) {
            try {
                Proxy proxy = proxySwitcher.getProxy(request);
                if (proxy != null) {
                    if (Http.autoSwitch) {
                        Socket socket = null;
                        try {
                            socket = new Socket();
                            socket.connect(proxy.address(), 5 * 1000);
                            OutputStream out = socket.getOutputStream();
                            out.write('\n');
                            out.flush();
                        }
                        finally {
                            if (socket != null)
                                socket.close();
                        }
                    }
                    log.debug("connect via proxy : " + proxy + " for " + request.getUrl());
                    conn = (HttpURLConnection) request.getUrl().openConnection(proxy);
                    conn.setConnectTimeout(Default_Conn_Timeout);
                    if (timeout > 0)
                        conn.setReadTimeout(timeout);
                    else
                        conn.setReadTimeout(Default_Read_Timeout);
                    return;
                }
            }
            catch (IOException e) {
                if (!Http.autoSwitch) {
                    throw e;
                }
                log.info("Test proxy FAIl, fallback to direct connection", e);
            }
        }
        conn = (HttpURLConnection) request.getUrl().openConnection();
        conn.setConnectTimeout(Default_Conn_Timeout);
        conn.setRequestMethod(request.getMethod().name());
        if (timeout > 0)
            conn.setReadTimeout(timeout);
        else
            conn.setReadTimeout(Default_Read_Timeout);
    }

    protected void setupRequestHeader() {
        URL url = request.getUrl();
        String host = url.getHost();
        if (url.getPort() > 0 && url.getPort() != 80)
            host += ":" + url.getPort();
        conn.setRequestProperty("Host", host);
        Header header = request.getHeader();
        if (null != header)
            for (Entry<String, String> entry : header.getAll())
                conn.addRequestProperty(entry.getKey(), entry.getValue());
    }

    public Sender setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

}
