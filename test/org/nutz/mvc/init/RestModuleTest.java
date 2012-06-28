package org.nutz.mvc.init;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.mvc.AbstractMvcTest;
import org.nutz.mvc.init.conf.RestModule;

public class RestModuleTest extends AbstractMvcTest {

    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", RestModule.class.getName());
    }

    @Test
    public void test_abc_get() throws Exception {
        request.setPathInfo("/abc");
        request.setMethod("Get");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("get", re);
    }

    @Test
    public void test_abc_post() throws Exception {
        request.setPathInfo("/abc");
        request.setMethod("Post");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("post", re);
    }

    @Test
    public void test_abc_put() throws Exception {
        request.setPathInfo("/abc");
        request.setMethod("PUT");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("put", re);
    }

    @Test
    public void test_abc_delete() throws Exception {
        request.setPathInfo("/abc");
        request.setMethod("Delete");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("delete", re);
    }

    @Test
    public void test_xyz_get() throws Exception {
        request.setPathInfo("/xyz");
        request.setMethod("Get");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("get&post", re);
    }

    @Test
    public void test_xyz_post() throws Exception {
        request.setPathInfo("/xyz");
        request.setMethod("Post");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("get&post", re);
    }

    @Test
    public void test_xyz_put() throws Exception {
        request.setPathInfo("/xyz");
        request.setMethod("PUT");
        servlet.service(request, response);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void test_xyz_delete() throws Exception {
        request.setPathInfo("/xyz");
        request.setMethod("Delete");
        servlet.service(request, response);
        assertEquals(404, response.getStatus());
    }

    @Test
    public void test_pathArgs_01() throws Exception {
        request.setPathInfo("/a/45/b/23/c/xyz");
        servlet.service(request, response);
        String re = response.getAsString();
        assertEquals("xyz?a=45&b=23", re);
    }
}
