package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 视图接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface View {

    void render(HttpServletRequest req, HttpServletResponse resp, Object obj) throws Throwable;

}
