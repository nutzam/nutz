package org.nutz.http.sender;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class PostSender extends Sender {

    public PostSender(Request request) {
        super(request);
    }

    @Override
    public Response send() throws HttpException {
        try {
            openConnection();
            InputStream ins = request.getInputStream();
            setupRequestHeader();
            if (ins != null
                && request.getHeader() != null
                && ins instanceof ByteArrayInputStream
                && this.request.getHeader().get("Content-Length") == null)
                conn.addRequestProperty("Content-Length", "" + ins.available());
            setupDoInputOutputFlag();
            if (null != ins) {
                OutputStream ops = Streams.buff(getOutputStream());
                Streams.write(ops, ins);
                Streams.safeClose(ins);
                Streams.safeFlush(ops);
                Streams.safeClose(ops);
            }
            return createResponse(getResponseHeader());
        }
        catch (Exception e) {
            throw new HttpException(request.getUrl().toString(), e);
        }
    }

    @Override
    public int getEstimationSize() throws IOException {
        if (request.getInputStream() != null) {
            return request.getInputStream().available();
        } else {
            if (null != request.getData()) {
                return request.getData().length;
            }
            try {
                return request.getURLEncodedParams().getBytes(request.getEnc()).length;
            }
            catch (UnsupportedEncodingException e) {
                throw Lang.wrapThrow(e);
            }
        }
    }
}
