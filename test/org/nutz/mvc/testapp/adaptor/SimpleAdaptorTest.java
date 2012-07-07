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
}
