package org.nutz.mvc.testapp.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class Issue1212MappingTest extends BaseWebappTest {

    @Test
    public void test_issue_1212() {
        get("/mapping/issue1212/sayhi");
        // 还原这个feature，这里应该是404
        assertEquals(404, resp.getStatus());
    }
    
    @Test
    public void test_issue_1337_1() {
        get("/mapping/issue1337/sayhi");
        // sayhi override了父类，所以是200
        assertEquals(200, resp.getStatus());
    }
    
    @Test
    public void test_issue_1337_2() {
        get("/mapping/issue1337/saybye");
        // saybye 没有override了父类，所以是404
        assertEquals(404, resp.getStatus());
    }
}
