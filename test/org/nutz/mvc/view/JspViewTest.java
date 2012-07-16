package org.nutz.mvc.view;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.Mvcs;

public class JspViewTest {

    @Before
    public void before() {
        Mvcs.setServletContext(Mock.servlet.context());
    }

    @Test
    public void test_name() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
        JspView fv = new JspView("abc.bcd");
        fv.render(req, null, null);
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());
    }

    @Test
    public void test_req_path() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
        req.setPathInfo("/abc/bcd.do");
        JspView fv = new JspView(null);
        fv.render(req, null, null);
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());
    }

    @Test
    public void test_req_path2() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
        req.setPathInfo("/abc/bcd.do");
        JspView fv = new JspView("");
        fv.render(req, null, null);
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());
    }

    @Test
    public void test_req_path3() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
        req.setPathInfo("/abc/bcd.do");
        JspView fv = new JspView("  \r\n\t  ");
        fv.render(req, null, null);
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());
    }

    @Test
    public void test_dest_path() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
        JspView fv = new JspView("/abc/bcd.jsp");
        fv.render(req, null, null);
        assertEquals("/abc/bcd.jsp", req.getDispatcherTarget());
    }
}
