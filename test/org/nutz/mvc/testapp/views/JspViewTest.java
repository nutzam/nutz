package org.nutz.mvc.testapp.views;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

/**
 * For ViewTestModule
 *
 */
public class JspViewTest extends BaseWebappTest {

    @Test
    public void test_simple(){
        get("/views/jsp");
        assertEquals("null", resp.getContent());
        get("/views/jsp2");
        assertEquals("null", resp.getContent());
        get("/views/jsp3");
        assertEquals("null", resp.getContent());
        get("/views/jsp4");
        assertEquals("null", resp.getContent());
    }
}
