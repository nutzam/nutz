package org.nutz.mvc.testapp.adaptor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class SimpleAdaptorTest extends BaseWebappTest {

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
     * Githut : #352
     */
    @Test
    public void test_reader_as_string() {
        resp = post("/adaptor/reader", "I am abc");
        if (resp.getStatus() != 200) {
            fail();
        }
        assertEquals("I am abc", resp.getContent());
    }
}
