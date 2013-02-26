package org.nutz.mvc.view;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.View;

/**
 * 将数据对象直接写入 HTTP 响应
 * <p>
 * <h2>数据对象可以是如下类型:</h2>
 * <ol>
 * <li><b>null</b> - 什么都不做
 * <li><b>File</b> - 文件,以下载方法返回,文件名将自动设置
 * <li><b>byte[]</b> - 按二进制方式写入HTTP响应流
 * <li><b>InputStream</b> - 按二进制方式写入响应流，并关闭 InputStream
 * <li><b>char[]</b> - 按文本方式写入HTTP响应流
 * <li><b>Reader</b> - 按文本方式写入HTTP响应流，并关闭 Reader
 * <li><b>默认的</b> - 直接将对象 toString() 后按文本方式写入HTTP响应流
 * </ol>
 * <p>
 * <h2>ContentType 支持几种缩写:</h2>
 * <ul>
 * <li><b>xml</b> - 表示 <b>text/xml</b>
 * <li><b>html</b> - 表示 <b>text/html</b>
 * <li><b>htm</b> - 表示 <b>text/html</b>
 * <li><b>stream</b> - 表示 <b>application/octet-stream</b>
 * <li><b>默认的</b>(即 '@Ok("raw")' ) - 将采用 <b>ContentType=text/plain</b>
 * </ul>
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class RawView implements View {
    
    private static final Log log = Logs.get();

    private String contentType;

    public RawView(String contentType) {
        if (Strings.isBlank(contentType))
            contentType = "text/plain";
        this.contentType = Strings.sNull(contentTypeMap.get(contentType.toLowerCase()), contentType);
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
    	//如果用户自行设置了,那就不要再设置了!
    	if (resp.getContentType() == null)
    		resp.setContentType(contentType);
        if (obj == null)
            return;
        //文件
        if (obj instanceof File) {
            File file = (File)obj;
            if (log.isDebugEnabled())
                log.debug("File downloading ... " +  file.getAbsolutePath());
            if (!file.exists() && file.isDirectory()) {
                resp.sendError(404);
                return;
            }
            file = file.getAbsoluteFile();
            String filename = URLEncoder.encode(file.getName(), Encoding.UTF8);
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            resp.setHeader("Content-Length", "" + file.length());
            Streams.writeAndClose(resp.getOutputStream(), Streams.fileIn(file));
        }
        // 字节数组
        else if (obj instanceof byte[]) {
            resp.setHeader("Content-Length", "" + ((byte[])obj).length);
            Streams.writeAndClose(resp.getOutputStream(), (byte[])obj);
        }
        // 字符数组
        else if (obj instanceof char[]) {
            Writer writer = resp.getWriter();
            writer.write((char[]) obj);
            writer.flush();
        }
        // 文本流
        else if (obj instanceof Reader) {
            Streams.writeAndClose(resp.getWriter(), (Reader)obj);
        }
        // 二进制流
        else if (obj instanceof InputStream) {
            Streams.writeAndClose(resp.getOutputStream(), (InputStream) obj);
        }
        // 普通对象
        else {
            byte[] data = String.valueOf(obj).getBytes(Encoding.UTF8);
            resp.setHeader("Content-Length", "" + data.length);
            Streams.writeAndClose(resp.getOutputStream(), data);
        }
    }

    private static final Map<String, String> contentTypeMap = new HashMap<String, String>();

    static {
        contentTypeMap.put("xml","application/xml");
        contentTypeMap.put("html", "text/html");
        contentTypeMap.put("htm", "text/html");
        contentTypeMap.put("stream", "application/octet-stream");
        contentTypeMap.put("js", "application/javascript");
        contentTypeMap.put("json", "application/json");
    }
}
