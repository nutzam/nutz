package org.nutz.mvc.testapp.views;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class ForwardViewTest extends BaseWebappTest {

    @Test
    public void test_simple() {
        get("/views/for?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());
        
        get("/views/for2?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());

        get("/views/for3?to=base");
        assertEquals(200, resp.getStatus());
        assertEquals(getContextPath(), resp.getContent());
    }
}
