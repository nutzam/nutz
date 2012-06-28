package org.nutz.http.sender;

import java.io.InputStream;
import java.io.OutputStream;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Streams;

public class PostSender extends Sender {

    public PostSender(Request request) {
        super(request);
    }

    @Override
    public Response send() throws HttpException {
        try {
            openConnection();
            setupRequestHeader();
            setupDoInputOutputFlag();
            InputStream ins = request.getInputStream();
            if(null!=ins){
                OutputStream ops = Streams.buff(conn.getOutputStream());
                Streams.write(ops, ins, 8192);
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

}
