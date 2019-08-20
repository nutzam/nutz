package org.nutz.http.sender;

import java.io.File;

import org.nutz.http.Request;
import org.nutz.http.Sender;
import org.nutz.http.SenderFactory;

public class DefaultSenderFactory implements SenderFactory {
    
    public Sender create(Request request) {
        if (request.isGet())
            return new GetSender(request);
        if (request.isDelete()) {
            if (request.getParams() != null || request.getData() != null || request.hasInputStream())
                return new PostSender(request);
            return new GetSender(request);
        }
        if ((request.isPost() || request.isPut()) && (request.getParams() != null)) {
            for (Object val : request.getParams().values()) {
                if (val instanceof File || val instanceof File[]) {
                    return new FilePostSender(request);
                }
            }
        }
        return new PostSender(request);
    }
}