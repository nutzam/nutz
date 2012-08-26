package org.nutz.mvc.adaptor;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.nutz.lang.Encoding;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.mock.Mock;
import org.nutz.mvc.AbstractMvcTest;

public class JsonAdaptorTest extends AbstractMvcTest {

    @Override
    protected void initServletConfig() {
        servletConfig.addInitParameter("modules", "org.nutz.mvc.adaptor.meta.BaseModule");
    }

    private void initreq(String path, String json) {
        request.setPathInfo(path);
        request.setInputStream(Mock.servlet.ins(new StringInputStream(json, Encoding.CHARSET_UTF8)));
    }
    
    @Test
    public void test_mapobj() throws ServletException, IOException {
        String path = "/json/map/obj";
        String json = "{map:{a:{name:'a'},b:{name:'b'},c:{name:'c'}}}";
        initreq(path, json);
        servlet.service(request, response);
        assertEquals(3,response.getAsInt());
    }
    
    @Test
    public void test_array() throws ServletException, IOException {
        String path = "/json/array";
        String json = "[{name:'a'},{name:'b'},{name:'c'}]";
        initreq(path, json);
        servlet.service(request, response);
        System.out.println(response.getAsString());
        assertEquals(3,response.getAsInt());
    }
    
    @Test
    public void test_list() throws ServletException, IOException {
        String path = "/json/list";
        String json = "[{name:'a'},{name:'b'},{name:'c'}]";
        initreq(path, json);
        servlet.service(request, response);
        assertEquals(3,response.getAsInt());
    }

    @Test
    public void test_hello() throws ServletException, IOException {
        String path = "/json/hello";
        String json = "{pet : {name:'测试'}}";
        initreq(path, json);
        servlet.service(request, response);
        assertEquals("\"!!测试!!\"", response.getAsString());
    }

    
    @Test
    public void test_map() throws ServletException, IOException {
        String path = "/json/map";
        String json = "{a:3,b:4,e:5}";
        initreq(path, json);
        servlet.service(request, response);
        assertEquals(3,response.getAsInt());
    }
    
}
