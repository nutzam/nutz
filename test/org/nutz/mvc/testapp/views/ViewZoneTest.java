package org.nutz.mvc.testapp.views;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class ViewZoneTest extends BaseWebappTest  {

    @Test
    public void test_view_resp() {
        get("/views/resp/to/1");
        assertEquals("hi", resp.getContent());
        get("/views/resp/to/2");
        assertEquals(200, resp.getStatus());
        assertEquals("{\"name\":\"wendal\"}", resp.getContent());
    }
}
