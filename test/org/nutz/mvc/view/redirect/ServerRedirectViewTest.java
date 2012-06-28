package org.nutz.mvc.view.redirect;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;

public class ServerRedirectViewTest extends AbstractMvcTest{
    
    @Override
    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", "org.nutz.mvc.view.redirect.MainModule");
    }

    @Test
    public void testRender() throws Throwable{
        request.setPathInfo("/register.nut");
        servlet.service(request, response);
        System.out.println(response.getHeader("Location"));
        assertTrue(response.getHeader("Location").endsWith("/jsp/user/information.nut?id=373"));
    }

    @Test
    public void testP_in_path() throws Throwable{
        request.setPathInfo("/login.nut");
        request.addParameter("name", "wendal");
        request.addParameter("password", "123456");
        servlet.service(request, response);
        System.out.println(response.getHeader("Location"));
        assertTrue(response.getHeader("Location").endsWith("/jsp/user/wendal"));
    }
}
