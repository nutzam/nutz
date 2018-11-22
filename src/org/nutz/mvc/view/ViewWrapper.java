package org.nutz.mvc.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

/**
 * 组合一个视图以及其渲染对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ViewWrapper implements View {

    public ViewWrapper(View view, Object data) {
        this.view = view;
        this.data = data;
    }

    private View view;

    private Object data;

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {
        view.render(req, resp, data);
    }

    public Object getData() {
        return data;
    }
}
