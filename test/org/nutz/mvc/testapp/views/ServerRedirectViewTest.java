package org.nutz.mvc.testapp.views;

import static org.junit.Assert.*;


import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class ServerRedirectViewTest extends BaseWebappTest {

    @Test
    public void test_simple() {
        get("/views/red?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());
        
        get("/views/red2?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());

        get("/views/red3?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());
    }
}
