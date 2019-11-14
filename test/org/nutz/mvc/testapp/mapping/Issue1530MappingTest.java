package org.nutz.mvc.testapp.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class Issue1530MappingTest extends BaseWebappTest {
    
    @Test
    public void test_issue_1530() {
        get("/mapping/issue1530/v1/yourname/wendal");
        assertEquals(200, resp.getStatus());
        assertEquals("v1-wendal", resp.getContent());
        
        get("/mapping/issue1530/v2/yourname/30");
        assertEquals(200, resp.getStatus());
        assertEquals("v2-30", resp.getContent());
    }
}
