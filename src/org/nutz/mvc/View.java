package org.nutz.mvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 视图接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface View {

    void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable;

}
