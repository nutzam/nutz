package org.nutz.lang.util;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;

public class MultiLinePropertiesTest extends Assert {

    @Test
    public void test_issue1096() throws IOException {
        MultiLineProperties props = new MultiLineProperties(new InputStreamReader(getClass().getResourceAsStream("issue1096.properties")));
        assertNotNull(props);
        assertEquals(4, props.size());
        assertEquals("1234567890", props.get("abc"));
        assertEquals("abc", props.get("def"));
        assertEquals("hi", props.get("dao"));
        assertEquals("", props.get("dao2"));
    }

    @Test
    public void test_properties_unicode() throws IOException {
        MultiLineProperties props = new MultiLineProperties(new InputStreamReader(getClass().getResourceAsStream("unicode.properties")));
        assertNotNull(props);
        assertEquals(2, props.size());
        assertEquals("中文", props.get("chinese"));
        assertEquals("最棒的", props.get("nutz"));
    }
}
