package org.nutz.mvc.init;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.RequestPath;

public class MvcBaseTest extends AbstractMvcTest {

    @Override
    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", "org.nutz.mvc.init.module.MainModule");
    }

    // zzh: NutServlet.ok 属性被我删掉了，它似乎没啥用了
    // TODO 发布前删掉下面的注释
    // @Test
    // public void testIsOK() throws Throwable {
    // Method method = NutServlet.class.getDeclaredMethod("isOk");
    // method.setAccessible(true);
    // assertTrue((Boolean) method.invoke(servlet));
    //
    // request.setPathInfo("/base/login.nut");
    // servlet.service(request, response);
    // assertEquals("true", response.getAsString());
    // }

    @Test
    public void testAnotherModule() throws Throwable {
        request.setPathInfo("/two/abc");
        servlet.service(request, response);
        assertEquals("\"haha\"", response.getAsString());
    }

    @Test
    public void testPointPath() throws Throwable {
        request.setPathInfo("/1.2/say.nutz");
        RequestPath path = Mvcs.getRequestPathObject(request);
        assertNotNull(path);
        assertEquals("/1.2/say", path.getPath());
        assertEquals("nutz", path.getSuffix());

        request.setPathInfo("/1.2/say");
        path = Mvcs.getRequestPathObject(request);
        assertNotNull(path);
        assertEquals("/1.2/say", path.getPath());

        request.setPathInfo("/1.2/say.po/");
        path = Mvcs.getRequestPathObject(request);
        assertNotNull(path);
        assertEquals("/1.2/say.po/", path.getPath());

        request.setPathInfo("/1.2/say.po/.nut");
        path = Mvcs.getRequestPathObject(request);
        assertNotNull(path);
        assertEquals("/1.2/say.po/", path.getPath());
    }

    @Test
    public void testRequestParms_error() throws Throwable {
        request.setPathInfo("/two/login.nutz");
        request.addParameter("username", "wendal");
        request.addParameter("password", "123456");
        request.addParameter("authCode", "Nutz");
        servlet.service(request, response);
        String resp = response.getAsString();
        System.out.println(resp);
        assertTrue(resp.indexOf("NumberFormatException") > -1);
    }

    @Test
    public void testRequestParms() throws Throwable {
        request.setPathInfo("/two/login.nutz");
        request.addParameter("username", "wendal");
        request.addParameter("password", "123456");
        request.addParameter("authCode", "236475");
        servlet.service(request, response);
        assertEquals("true", response.getAsString());
    }
    
    @Test
    public void test_CheckSession() throws Throwable {
        request.setPathInfo("/two/need.nutz");
        servlet.service(request, response);
        assertEquals("/two/abc", response.getHeader("Location"));
    }
    
    @Test
    public void test_NotPublicClass() throws Throwable {
        request.setPathInfo("/here");
        servlet.service(request, response);
        assertEquals("", response.getAsString());
    }
    
    @Test
    public void test_PathArgs() throws Throwable {
        request.setPathInfo("/two/pathme/123.nutz");
        servlet.service(request, response);
        assertEquals("\"123\"", response.getAsString());
    }
}
