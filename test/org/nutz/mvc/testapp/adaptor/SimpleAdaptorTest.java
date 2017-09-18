package org.nutz.mvc.testapp.adaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.TimeZone;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.testapp.BaseWebappTest;
import org.nutz.mvc.testapp.classes.bean.Issue1277;

public class SimpleAdaptorTest extends BaseWebappTest {

    @Test
    public void test_issue_543() {
        get("/adaptor/github/issue/543?d=20120924");
        assertEquals(200, resp.getStatus());

        long ms = Times.ams("2012-09-24", TimeZone.getTimeZone("Asia/Shanghai"));
        long rems = Long.parseLong(resp.getContent());
        assertEquals(ms, rems);
    }

    @Test
    public void test_err_param() {
        get("/adaptor/err/param?id=ABC");
        assertEquals(200, resp.getStatus());

        get("/adaptor/err/param/ABC");
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void test_err_param_anywhere() {
        get("/adaptor/err/param/anywhere?id=ABC");
        assertEquals(200, resp.getStatus());

        get("/adaptor/err/param/anywhere/ABC");
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void test_err_param_with_pathargs() {
        get("/adaptor/err/param/pathargs/a?id=ABC");
        assertEquals(200, resp.getStatus());

        get("/adaptor/err/param/pathargs/a/ABC");
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void test_multi_err_ctxs() {
        get("/adaptor/multi/err/ctxs/a?id=ABC");
        assertEquals(200, resp.getStatus());

        get("/adaptor/multi/err/ctxs/a/ABC");
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void test_multi_err_ctxs2() {
        get("/adaptor/multi/err/ctxs2/a/b?id=ABC");
        assertEquals(200, resp.getStatus());

        get("/adaptor/multi/err/ctxs2/a/b/ABC");
        assertEquals(200, resp.getStatus());
    }

    @Test
    public void test_json_map_type() {
        resp = post("/adaptor/json/type", "{'abc': 123456}");
        if (resp.getStatus() != 200) {
            fail();
        }
    }

    /*
     * Githut : #352
     */
    @Test
    public void test_inputstream_as_string() {
        resp = post("/adaptor/ins", "I am abc");
        if (resp.getStatus() != 200) {
            fail();
        }
        assertEquals("I am abc", resp.getContent());
    }

    /*
     * Github : #352
     */
    @Test
    public void test_reader_as_string() {
        resp = post("/adaptor/reader", "I am abc");
        if (resp.getStatus() != 200) {
            fail();
        }
        assertEquals("I am abc", resp.getContent());
    }
    
    /**
     * Github #768 入口方法的参数的默认值
     */
    @Test
    public void test_default_value() {
    	resp = get("/adaptor/default_value?abc=123");
    	assertEquals(200, resp.getStatus());
    	assertEquals("123", resp.getContent());
    	

    	resp = get("/adaptor/default_value");
    	assertEquals(200, resp.getStatus());
    	assertEquals("123456", resp.getContent());
    }
    
    /**
     * Json适配器未正确处理AdaptorErrorContext
     */
    @Test
    public void test_json_err_ctx() {
        resp = post("/adaptor/err_ctx", "{}");
        assertEquals(200, resp.getStatus());
        assertEquals("true", resp.getContent());
        
        resp = post("/adaptor/err_ctx", "{1234,3445}");
        assertEquals(200, resp.getStatus());
        assertEquals("false", resp.getContent());
    }
    
    @Test
    public void test_sql_date() {
    	resp = post("/adaptor/sqldate", "checkDate=2016-01-29");
        assertEquals(200, resp.getStatus());
        assertEquals("2016-01-29", resp.getContent());
    }
    
    @Test
    public void test_array_without_param() {
        assertEquals(200, get("/adaptor/param_without_param").getStatus());
        assertEquals("[\"1\", \"2\", \"4\", \"3\"]".replaceAll(" ", ""), get("/adaptor/param_without_param?uids=1,2,4,3").getContent().replaceAll(" ", ""));
    }

    @Test
    public void test_object_without_param() {
        assertEquals(200, get("/adaptor/object_without_param").getStatus());
        assertEquals("{\"name\": \"object\"}".replaceAll(" ", ""), get("/adaptor/object_without_param?name=object").getContent().replaceAll(" ", ""));
    }

    @Test
    public void test_path_args_and_object_without_param() {
        assertEquals(200, get("/adaptor/path_args_and_object_without_param/1").getStatus());
        assertEquals("{\"name\": \"1\"}".replaceAll(" ", ""), get("/adaptor/path_args_and_object_without_param/1?name=object").getContent().replaceAll(" ", ""));
    }

    @Test
    public void issue_1069() {
        resp = post("/adaptor/issue1069", "");
        assertEquals(200, resp.getStatus());
        assertEquals("", resp.getContent());
        

        resp = post("/adaptor/issue1069", "showAdd=");
        assertEquals(200, resp.getStatus());
        assertEquals("", resp.getContent());
    }
    
    @Test
    public void issue_1267() {
        resp = post("/adaptor/issue1267", new NutMap("time", "Thu May 25 2017 07:16:32 GMT+0800 (CST)"));
        assertEquals(200, resp.getStatus());
        System.out.println(resp.getContent());
        //assertEquals("1495667792000", resp.getContent());
    }
    
    @Test
    public void issue_1277() {
        resp = post("/adaptor/issue1277", new NutMap("agex", "124"));
        assertEquals(200, resp.getStatus());
        String str = resp.getContent();
        Issue1277 issue = Json.fromJson(Issue1277.class, str);
        assertEquals("abc", issue.name);
        assertEquals(123, issue.age);
        //assertEquals("1495667792000", resp.getContent());
    }
    
    @Test
    public void issue_1310() {
        resp = post("/adaptor/issue1310", new NutMap("age", "123"));
        assertEquals(200, resp.getStatus());
        String str = resp.getContent();
        Issue1277 issue = Json.fromJson(Issue1277.class, str);
        assertEquals(123, issue.age);
        //assertEquals("1495667792000", resp.getContent());
    }
}
