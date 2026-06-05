package org.nutz.mvc.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.nutz.mvc.View;

public class VoidView implements View {

    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {}

}
