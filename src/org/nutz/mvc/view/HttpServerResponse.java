package org.nutz.mvc.view;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.http.Http;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class HttpServerResponse implements Cloneable {

    private static final Log log = Logs.get();

    private int statusCode;

    private String statusText;

    private Map<String, String> header;

    private byte[] body;

    public HttpServerResponse() {
        this.header = new HashMap<String, String>();
    }

    public HttpServerResponse clone() {
        HttpServerResponse re = new HttpServerResponse();
        re.statusCode = statusCode;
        re.statusText = statusText;
        re.header = new HashMap<String, String>();
        if (header != null)
            re.header.putAll(header);
        re.body = body;
        return re;
    }

    private static final Pattern _P = Pattern.compile("^HTTP/1.\\d\\s+(\\d+)(\\s+(.*))?$");

    public void updateBy(String str) {
        try {
            // 如果以 HTTP/1.x 开头，则认为是要输出 HTTP 头
            if (str.startsWith("HTTP/1.")) {
                int pos = str.indexOf('\n');
                // 读取返回码
                String sStatus = str.substring(0, pos);
                Matcher m = _P.matcher(sStatus);
                if (!m.find())
                    throw Lang.makeThrow("invalid HTTP status line: %s", sStatus);

                statusCode = Integer.parseInt(m.group(1));
                statusText = Strings.trim(m.group(3));
                if (Strings.isBlank(statusText))
                    statusText = Http.getStatusText(statusCode);

                // 读取头部信息
                pos++;
                int end;
                while ((end = str.indexOf('\n', pos)) > pos) {
                    String line = str.substring(pos, end);
                    // 拆分一下行
                    int p2 = line.indexOf(':');
                    String key = Strings.trim(line.substring(0, p2));
                    String val = Strings.trim(line.substring(p2 + 1));
                    header.put(key, val);

                    // 指向下一行
                    pos = end + 1;
                }

                // 头部一定读取结束了，向下跳一行
                pos++;

                // 读取剩余作为 body
                if (pos < str.length()) {
                    this.body = str.substring(pos).getBytes(Encoding.UTF8);
                }

            }
            // 否则就认为是 HTTP 200
            else {
                if (statusCode <= 0) {
                    this.updateCode(200, null);
                }
                this.body = str.getBytes(Encoding.UTF8);
            }
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public void update(Map<?, ?> map) {
        for (Map.Entry<?, ?> en : map.entrySet()) {
            String key = en.getKey().toString();
            Object val = en.getValue();

            if (null == val)
                continue;

            // statusCode
            if ("statusCode".equals(key)) {
                this.statusCode = Castors.me().castTo(val, Integer.class);
                this.statusText = Http.getStatusText(statusCode);
            }
            // statusText
            else if ("statusText".equals(key)) {
                this.statusText = val.toString();
            }
            // body
            else if ("body".equals(key)) {
                try {
                    body = val.toString().getBytes(Encoding.UTF8);
                }
                catch (UnsupportedEncodingException e) {
                    throw Lang.wrapThrow(e);
                }
            }
            // 其他作为 Header
            else {
                this.header.put(key.toUpperCase(), val.toString());
            }
        }

    }

    public void updateCode(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = Strings.sNull(statusText, Http.getStatusText(statusCode));
    }

    public void updateBody(String body) {
        if (!Strings.isBlank(body))
            try {
                this.body = body.getBytes(Encoding.UTF8);
            }
            catch (UnsupportedEncodingException e) {
                throw Lang.wrapThrow(e);
            }
    }

    public void render(HttpServletResponse resp) {
        resp.setStatus(statusCode);

        // 标记是否需要sendError
        boolean flag = statusCode >= 400;

        if (null != header && header.size() > 0) {
            for (Map.Entry<String, String> en : header.entrySet()) {
                resp.setHeader(en.getKey(), en.getValue());
            }
            flag = false;
        }

        if (body != null) {
            resp.setContentLength(body.length);
            OutputStream out;
            try {
                out = resp.getOutputStream();
            }
            catch (IOException e) {
                throw Lang.wrapThrow(e);
            }
            Streams.writeAndClose(out, body);
            flag = false;
        }

        if (flag) {
            try {
                resp.sendError(statusCode);
            }
            catch (IOException e) {
                log.debugf("sendError(%d) failed -- %s", statusCode, e.getMessage());
            }
        }
    }

}