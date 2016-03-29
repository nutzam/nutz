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
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class RawView2 extends RawView {

    private static final Log log = Logs.get();

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
            resp.setHeader("Content-Range",
                           String.format("bytes %d-%d/%d",
                                         rangeRange.start,
                                         rangeRange.end - 1,
                                         maxLen));
            OutputStream out = resp.getOutputStream();
            writeDownloadRange(in, out, rangeRange);
        }
        finally {
            Streams.safeClose(in);
        }
    }
}
