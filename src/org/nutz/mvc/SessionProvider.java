package org.nutz.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SessionProvider {

    HttpServletRequest filter(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext);
    
    void notifyStop();
}
