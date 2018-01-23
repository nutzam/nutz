package org.nutz.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilterOutputStream;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.nutz.http.sender.FilePostSender;
import org.nutz.http.sender.GetSender;
import org.nutz.http.sender.PostSender;
import org.nutz.lang.Lang;
import org.nutz.lang.stream.VoidInputStream;
import org.nutz.lang.util.Callback;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class Sender implements Callable<Response> {

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
        return create(url).setTimeout(timeout);
    }

    public static Sender create(Request request) {
        if (request.isGet() || request.isDelete())
            return new GetSender(request);
        if ((request.isPost() || request.isPut()) && request.getParams() != null) {
            for (Object val : request.getParams().values()) {
                if (val instanceof File || val instanceof File[]) {
                    return new FilePostSender(request);
                }
            }
        }
        return new PostSender(request);
    }

    public static Sender create(Request request, int timeout) {
        return create(request).setTimeout(timeout);
    }

    protected Request request;
    
    private int connTimeout;

    private int timeout;

    protected HttpURLConnection conn;
    
    protected HttpReqRespInterceptor interceptor = new Cookie();
    
    protected Callback<Response> callback;
    
    protected boolean followRedirects = true;
    
    protected SSLSocketFactory sslSocketFactory;

    protected Sender(Request request) {
        this.request = request;
    }
    
    protected Callback<Integer> progressListener;

    public abstract Response send() throws HttpException;

    protected Response createResponse(Map<String, String> reHeaders) throws IOException {
        Response rep = null;
        if (reHeaders != null) {
            rep = new Response(conn, reHeaders);
            String encoding = conn.getContentEncoding();
            if (rep.isOK()) {
                InputStream is1 = conn.getInputStream();
                InputStream is2 = null;
                // 如果采用了压缩,则需要处理否则都是乱码
                is2 = detectStreamEncode(encoding, is1);

                BufferedInputStream is = new BufferedInputStream(is2);
                rep.setStream(is);
            }

            else {
                try {
                    rep.setStream(detectStreamEncode(encoding, conn.getInputStream()));
                }
                catch (IOException e) {
                    try {
                        rep.setStream(detectStreamEncode(encoding, conn.getErrorStream()));
                    }
                    catch (Exception e1) {
                        rep.setStream(new VoidInputStream());
                    }
                }
            }
        }
        if (this.interceptor != null)
            this.interceptor.afterResponse(request, conn, rep);
        return rep;
    }
    
    protected InputStream detectStreamEncode(String encoding, InputStream ins) throws IOException {
        if (encoding != null && encoding.contains("gzip")) {
            return new GZIPInputStream(ins);
        } else if (encoding != null && encoding.contains("deflate")) {
            return new InflaterInputStream(ins, new Inflater(true));
        } else {
            return ins;
        }
    }

    protected Map<String, String> getResponseHeader() throws IOException {
        if (conn.getResponseCode() < 0)
            throw new IOException("Network error!! resp code=" + conn.getResponseCode());
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
    }

    protected void openConnection() throws IOException {
        if (this.interceptor != null)
            this.interceptor.beforeConnect(request);
        ProxySwitcher proxySwitcher = Http.proxySwitcher;
        int connTime = connTimeout > 0 ? connTimeout : Default_Conn_Timeout;
        if (proxySwitcher != null) {
            try {
                Proxy proxy = proxySwitcher.getProxy(request);
                if (proxy != null) {
                    if (Http.autoSwitch) {
                        Socket socket = null;
                        try {
                            socket = new Socket();
                            socket.connect(proxy.address(), connTime); //5 * 1000
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
                    conn.setConnectTimeout(connTime);
                    conn.setInstanceFollowRedirects(followRedirects);
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
        URL url = request.getUrl();
        String host = url.getHost();
        conn = (HttpURLConnection) url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            if (sslSocketFactory != null)
                ((HttpsURLConnection)conn).setSSLSocketFactory(sslSocketFactory);
            else if (Http.sslSocketFactory != null)
                ((HttpsURLConnection)conn).setSSLSocketFactory(Http.sslSocketFactory);
        }
        if (!Lang.isIPv4Address(host)) {
            if (url.getPort() > 0 && url.getPort() != 80)
                host += ":" + url.getPort();
            conn.addRequestProperty("Host", host);
        }
        conn.setConnectTimeout(connTime);
        if (request.getMethodString() == null)
        	conn.setRequestMethod(request.getMethod().name());
        else
        	conn.setRequestMethod(request.getMethodString());
        if (timeout > 0)
            conn.setReadTimeout(timeout);
        else
            conn.setReadTimeout(Default_Read_Timeout);
        conn.setInstanceFollowRedirects(followRedirects);
        if (interceptor != null)
            this.interceptor.afterConnect(request, conn);
    }

    protected void setupRequestHeader() {
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
    
    public Sender setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
        return this;
    }
    
    public int getConnTimeout() {
        return connTimeout;
    }

    public Sender setInterceptor(HttpReqRespInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }
    
    public Sender setCallback(Callback<Response> callback) {
        this.callback = callback;
        return this;
    }
    
    public Response call() throws Exception {
        Response resp = send();
        if (callback != null)
            callback.invoke(resp);
        return resp;
    }
    
    public Future<Response> send(Callback<Response> callback) throws HttpException {
        if (es == null)
            throw new IllegalStateException("Sender ExecutorService is null, Call setup first");
        this.callback = callback;
        return es.submit(this);
    }
    
    protected static ExecutorService es;
    
    public static ExecutorService setup(ExecutorService es) {
        if (Sender.es != null)
            shutdown();
        if (es == null)
            es = Executors.newFixedThreadPool(64);
        Sender.es = es;
        return es;
    }
    
    public static List<Runnable> shutdown() {
        ExecutorService _es = es;
        es = null;
        if (_es == null)
            return null;
        return _es.shutdownNow();
    }
    
    public static ExecutorService getExecutorService() {
        return es;
    }
    
    public Sender setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }
    
    protected OutputStream getOutputStream() throws IOException {
        OutputStream out = conn.getOutputStream();
        if (progressListener == null)
            return out;
        return new FilterOutputStream(out) {
            int count;
            public void write(byte[] b, int off, int len) throws IOException {
                super.write(b, off, len);
                count += len;
                progressListener.invoke(count);
            }
        };
    }
    
    public int getEstimationSize() throws IOException {
        return 0;
    }
    
    public Sender setProgressListener(Callback<Integer> progressListener) {
        this.progressListener = progressListener;
        return this;
    }
    
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
}
