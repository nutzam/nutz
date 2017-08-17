package org.nutz.mvc.view;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;

public class RawView2 extends RawView {

    protected DataInputStream in;

    protected int maxLen;

    protected String CONTENT_DISPOSITION;

    protected RawView2() {}

    public RawView2(String contentType, InputStream in, int maxLen) {
        super(contentType);
        this.in = new DataInputStream(in);
        this.maxLen = maxLen;
    }

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        try {
            if (resp.getContentType() == null)
                resp.setContentType(contentType);
            resp.addHeader("Connection", "close");
            String rangeStr = req.getHeader("Range");

            if (!resp.containsHeader("Content-Disposition")
                && !Strings.isBlank(CONTENT_DISPOSITION)) {
                resp.setHeader("Content-Disposition", CONTENT_DISPOSITION);
            }

            if (rangeStr == null) {
                resp.setContentLength(maxLen);
                Streams.writeAndClose(resp.getOutputStream(), in);
                return;
            }
            List<RangeRange> rs = new ArrayList<RawView.RangeRange>();
            if (!parseRange(rangeStr, rs, maxLen)) {
                resp.setStatus(416);
                return;
            }
            long totolSize = 0;
            for (RangeRange rangeRange : rs) {
                totolSize += (rangeRange.end - rangeRange.start);
            }
            resp.setStatus(206);
            if (rs.size() == 1) {
                // 单个Range的时候简单一些
                RangeRange rangeRange = rs.get(0);
                resp.setHeader("Accept-Ranges", "bytes");
                resp.setHeader("Content-Range", rangeRange.toString(maxLen));
                resp.setHeader("Content-Length", "" + totolSize);
                OutputStream out = resp.getOutputStream();
                writeDownloadRange(in, out, rangeRange);
            } else {
                String k = R.UU32().substring(0, 11);
                resp.setCharacterEncoding(null);
                resp.setHeader("Content-Type", "multipart/byteranges; boundary="+k);
                byte[] SLINE = ("--" + k + "\r\n").getBytes();
                byte[] CLINE = ("Content-Type: " + contentType + "\r\n").getBytes();
                // 计算ContentLength,蛋疼啊
                totolSize += "\r\n".getBytes().length;
                for (RangeRange rangeRange : rs) {
                    totolSize += SLINE.length;
                    totolSize += CLINE.length;
                    totolSize += ("Content-Range: " + rangeRange.toString(maxLen) + "\r\n\r\n").getBytes().length;
                    totolSize +=  "\r\n".getBytes().length;
                }
                totolSize += ("--" + k + "--\r\n").getBytes().length;
                resp.setHeader("Content-Length", "" + totolSize);
                
                RangeRange preRangeRange = null;
                OutputStream out = resp.getOutputStream();
                out.write("\r\n".getBytes());
                for (RangeRange rangeRange : rs) {
                    out.write(SLINE);
                    out.write(CLINE);
                    out.write(("Content-Range: " + rangeRange.toString(maxLen) + "\r\n\r\n").getBytes());
                    writeDownloadRange(in, out, rangeRange, preRangeRange);
                    out.write("\r\n".getBytes());
                    preRangeRange = rangeRange;
                }
                out.write(("--" + k + "--\r\n").getBytes());
            }
        }
        finally {
            Streams.safeClose(in);
        }
    }
}
