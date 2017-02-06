package org.nutz.mvc.testapp.mapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.nutz.mvc.testapp.BaseWebappTest;

public class Issue1212MappingTest extends BaseWebappTest {

    @Test
    public void test_issue_1212() {
        get("/mapping/issue1212/sayhi");
        assertEquals(200, resp.getStatus());
    }
}
