package org.nutz.mvc.testapp.adaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.TimeZone;

import org.junit.Test;
import org.nutz.lang.Times;
import org.nutz.mvc.testapp.BaseWebappTest;

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
    
//    @Test
//    public void test_sql_date() {
//    	resp = post("/adaptor/sqldate", "checkDate=2016-01-29");
//        assertEquals(200, resp.getStatus());
//        assertEquals("2016-01-29", resp.getContent());
//    }
}
