package org.nutz.mvc.view;

import org.nutz.mvc.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class VoidView implements View {

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
            throws Throwable {}

}
