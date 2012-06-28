package org.nutz.mvc.testapp;

import static org.junit.Assert.*;

import org.junit.Test;

public class BaseTest extends BaseWebappTest {

    @Test
    public void test_json_adaptor() {
        post("/adaptor/json/pet/array", "{pets:[{name:'zzh'},{name:'wendal'}]}");
        assertEquals("pets(2) array", resp.getContent());

        post("/adaptor/json/pet/list", "{pets:[{name:'zzh'},{name:'wendal'}]}");
        assertEquals("pets(2) list", resp.getContent());
    }

    @Test
    public void test_base() {
        get("/base.jsp");
        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());
    }

    @Test
    public void test_pathargs() {
        get("/common/pathArgs/Wendal");
        assertEquals("Wendal", resp.getContent());

        get("/common/pathArgs2/Wendal/12345/123456789/123/123.00/200.9/true/n");
        assertEquals("Wendal12345123456789123123200truen", resp.getContent());

        get("/common/pathArgs3/public/blog/200");
        assertEquals("public&200", resp.getContent());
        get("/common/pathArgs3/puZ");
        assertEquals("puZ&Z", resp.getContent());

        get("/common/pathArgs4/nutz?name=wendal");
        assertEquals("nutz&wendal", resp.getContent());

        get("/common/pathArgs5/nutz?user.name=wendal&user2.name=zozoh");
        assertEquals("nutz&wendal&zozoh", resp.getContent());
    }

    @Test
    public void test_param() {
        get("/common/param?id=" + Long.MAX_VALUE);
        assertEquals("" + Long.MAX_VALUE, resp.getContent());
    }

    @Test
    public void test_req_param() {
        get("/common/path?key=base");
        assertEquals(getContextPath(), resp.getContent());
    }

    // With EL
    @Test
    public void test_req_param2() {
        get("/common/path2?key=base");
        assertEquals("base", resp.getContent());
        get("/common/path2?key=T");
        assertEquals(getContextPath(), resp.getContent());
    }

    // 有用户报告测试 resp.getOutputStream失败
    @Test
    public void test_servlet_obj() {
        get("/common/servlet_obj");
        assertEquals(200, resp.getStatus());
    }
}
