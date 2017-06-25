package org.nutz.mvc.view;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.img.Images;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
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
 * <li><b>BufferedImage</b> - 按图片方式写入HTTP响应流，并关闭 Reader
 * <li><b>默认的</b> - 直接将对象 toString() 后按文本方式写入HTTP响应流
 * </ol>
 * <p>
 * <h2>ContentType 支持几种缩写:</h2>
 * <ul>
 * <li><b>xml</b> - 表示 <b>text/xml</b>
 * <li><b>html</b> - 表示 <b>text/html</b>
 * <li><b>htm</b> - 表示 <b>text/html</b>
 * <li><b>stream</b> - 表示 <b>application/octet-stream</b>
 * <li><b>js</b> - 表示 <b>application/javascript</b>
 * <li><b>json</b> - 表示 <b>application/json</b>
 * <li><b>pdf</b> -- 表示<b>application/pdf</b>
 * <li><b>jpeg</b> - 表示 <b>image/jpeg</b> 返回值是BufferedImage对象时自动转二进制流,质量为0.8f
 * <li><b>jpg</b> - 表示 <b>image/jpeg</b> 返回值是BufferedImage对象时自动转二进制流,质量为0.8f
 * <li><b>png</b> - 表示 <b>image/png</b> 返回值是BufferedImage对象时自动转二进制流
 * <li><b>webp</b> - 表示 <b>application/webp</b> 返回值是BufferedImage对象时自动转二进制流
 * <li><b>默认的</b>(即 '@Ok("raw")' ) - 将采用 <b>ContentType=text/plain</b>
 * </ul>
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class RawView implements View {

    private static final Log log = Logs.get();

    private static final int big4G = Integer.MAX_VALUE;

    public static final boolean DISABLE_RANGE_DOWNLOAD = false; // 禁用断点续传

    protected String contentType;

    protected RawView() {}

    public RawView(String contentType) {
        if (Strings.isBlank(contentType))
            contentType = "text/plain";
        this.contentType = Strings.sNull(contentTypeMap.get(contentType.toLowerCase()), contentType);
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        // 如果用户自行设置了,那就不要再设置了!
        if (resp.getContentType() == null) {
            if (!Lang.isAndroid
                && obj != null
                && obj instanceof BufferedImage
                && "text/plain".equals(contentType)) {
                contentType = contentTypeMap.get("png");
            }
            resp.setContentType(contentType);
        }
        if (obj == null)
            return;
        // 图片?难道是验证码?
        if (!Lang.isAndroid && obj instanceof BufferedImage) {
            OutputStream out = resp.getOutputStream();
            if (contentType.contains("png"))
                ImageIO.write((BufferedImage) obj, "png", out);
            // @see
            // https://code.google.com/p/webm/source/browse/java/src/main/java/com/google/imageio/?repo=libwebp&name=sandbox%2Fpepijnve%2Fwebp-imageio#imageio%2Fwebp
            else if (contentType.contains("webp"))
                ImageIO.write((BufferedImage) obj, "webp", out);
            else
                Images.writeJpeg((BufferedImage) obj, out, 0.8f);
            return;
        }
        // 文件
        else if (obj instanceof File) {
            File file = (File) obj;
            long fileSz = file.length();
            if (log.isDebugEnabled())
                log.debug("File downloading ... " + file.getAbsolutePath());
            if (!file.exists() || file.isDirectory()) {
                log.debug("File downloading ... Not Exist : " + file.getAbsolutePath());
                resp.sendError(404);
                return;
            }
            if (!resp.containsHeader("Content-Disposition")) {
                String filename = URLEncoder.encode(file.getName(), Encoding.UTF8);
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            }

            String rangeStr = req.getHeader("Range");
            OutputStream out = resp.getOutputStream();
            if (DISABLE_RANGE_DOWNLOAD
                || fileSz == 0
                || (rangeStr == null || !rangeStr.startsWith("bytes=") || rangeStr.length() < "bytes=1".length())) {
                resp.setHeader("Content-Length", "" + fileSz);
                Streams.writeAndClose(out, Streams.fileIn(file));
            } else {
                // log.debug("Range Download : " + req.getHeader("Range"));
                List<RangeRange> rs = new ArrayList<RawView.RangeRange>();
                if (!parseRange(rangeStr, rs, fileSz)) {
                    resp.setStatus(416);
                    return;
                }
                // 暂时只实现了单range
                if (rs.size() != 1) {
                    // TODO 完成多range的下载
                    log.info("multipart/byteranges is NOT support yet");
                    resp.setStatus(416);
                    return;
                }
                long totolSize = 0;
                for (RangeRange rangeRange : rs) {
                    totolSize += (rangeRange.end - rangeRange.start);
                }
                resp.setStatus(206);
                resp.setHeader("Content-Length", "" + totolSize);
                resp.setHeader("Accept-Ranges", "bytes");

                // 暂时只有单range,so,简单起见吧
                RangeRange rangeRange = rs.get(0);
                resp.setHeader("Content-Range", String.format("bytes %d-%d/%d",
                                                              rangeRange.start,
                                                              rangeRange.end - 1,
                                                              fileSz));
                writeFileRange(file, out, rangeRange);
            }
        }
        // 字节数组
        else if (obj instanceof byte[]) {
            resp.setHeader("Content-Length", "" + ((byte[]) obj).length);
            OutputStream out = resp.getOutputStream();
            Streams.writeAndClose(out, (byte[]) obj);
        }
        // 字符数组
        else if (obj instanceof char[]) {
            Writer writer = resp.getWriter();
            writer.write((char[]) obj);
            writer.flush();
        }
        // 文本流
        else if (obj instanceof Reader) {
            Streams.writeAndClose(resp.getWriter(), (Reader) obj);
        }
        // 二进制流
        else if (obj instanceof InputStream) {
            OutputStream out = resp.getOutputStream();
            Streams.writeAndClose(out, (InputStream) obj);
        }
        // 普通对象
        else {
            byte[] data = String.valueOf(obj).getBytes(Encoding.UTF8);
            resp.setHeader("Content-Length", "" + data.length);
            OutputStream out = resp.getOutputStream();
            Streams.writeAndClose(out, data);
        }
    }

    protected static final Map<String, String> contentTypeMap = new HashMap<String, String>();

    static {
        contentTypeMap.put("xml", "application/xml");
        contentTypeMap.put("html", "text/html");
        contentTypeMap.put("htm", "text/html");
        contentTypeMap.put("stream", "application/octet-stream");
        contentTypeMap.put("js", "application/javascript");
        contentTypeMap.put("json", "application/json");
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("webp", "image/webp");
    }

    public static class RangeRange {
        public RangeRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long start;
        public long end = -1;

    }

    public static final boolean parseRange(String rangeStr, List<RangeRange> rs, long maxSize) {
        rangeStr = rangeStr.substring("bytes=".length());
        String[] ranges = rangeStr.split(",");
        for (String range : ranges) {
            if (range == null || Strings.isBlank(range)) {
                log.debug("Bad Range -->    " + rangeStr);
                return false;
            }
            range = range.trim();
            try {
                // 首先是从后往前算的 bytes=-100 取最后100个字节
                if (range.startsWith("-")) {

                    // 注意,这里是负数
                    long end = Long.parseLong(range);
                    long start = maxSize + end;
                    if (start < 0) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, maxSize));
                    continue;
                }

                // 然后就是从开头到最后 bytes=1024-
                if (range.endsWith("-")) {
                    // 注意,这里是负数
                    long start = Long.parseLong(range.substring(0, range.length() - 1));
                    if (start < 0) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, maxSize));
                    continue;
                }

                // 哦也,是最标准的有头有尾?
                if (range.contains("-")) {
                    String[] tmp = range.split("-");
                    long start = Long.parseLong(tmp[0]);
                    long end = Long.parseLong(tmp[1]);
                    if (start > end) {
                        log.debug("Bad Range -->    " + rangeStr);
                        return false;
                    }
                    rs.add(new RangeRange(start, end + 1)); // 这里需要调查一下
                } else {
                    // 操!! 单个字节?!!
                    long start = Long.parseLong(range);
                    rs.add(new RangeRange(start, start + 1));
                }
            }
            catch (Throwable e) {
                log.debug("Bad Range -->    " + rangeStr, e);
                return false;
            }
        }
        return !rs.isEmpty();
    }

    public static void writeDownloadRange(DataInputStream in,
                                          OutputStream out,
                                          RangeRange rangeRange) {
        try {
            if (rangeRange.start > 0) {
                long start = rangeRange.start;
                while (start > 0) {
                    if (start > big4G) {
                        start -= big4G;
                        in.skipBytes(big4G);
                    } else {
                        in.skipBytes((int) start);
                        break;
                    }
                }
            }
            byte[] buf = new byte[8192];
            BufferedInputStream bin = new BufferedInputStream(in);
            long pos = rangeRange.start;
            int len = 0;
            while (pos < rangeRange.end) {
                if (rangeRange.end - pos > 8192) {
                    len = bin.read(buf);
                } else {
                    len = bin.read(buf, 0, (int) (rangeRange.end - pos));
                }
                if (len == -1) {// 有时候,非常巧合的,文件已经读取完,就悲剧开始了...
                    break;
                }
                if (len > 0) {
                    out.write(buf, 0, len);
                    pos += len;
                }
            }
            out.flush();
        }
        catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
    }

    public static void writeFileRange(File file, OutputStream out, RangeRange rangeRange) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fin);
            writeDownloadRange(in, out, rangeRange);
        }
        catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(fin);
        }
    }
}
