package org.nutz.http;

import java.net.HttpURLConnection;

public interface HttpReqRespInterceptor {

    void beforeConnect(Request request);
    
    void afterConnect(Request request, HttpURLConnection conn);
    
    void afterResponse(Request request, HttpURLConnection conn, Response response);
}
