package org.nutz.lang.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Encoding;

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
        assertEquals(4, props.size());
        assertEquals("中文", props.get("chinese"));
        assertEquals("最棒的", props.get("nutz"));
    }
    

    @Test
    public void test_properties_print_and_change() throws IOException {
        MultiLineProperties props = new MultiLineProperties(new InputStreamReader(getClass().getResourceAsStream("unicode.properties")));
        assertNotNull(props);
        assertEquals(4, props.size());
        assertEquals("中文", props.get("chinese"));
        assertEquals("最棒的", props.get("nutz"));
        
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        props.print(bao);
        System.out.println(props.entrySet().size());
        
        System.out.println(new String(bao.toByteArray()));
        
        props = new MultiLineProperties(new InputStreamReader(new ByteArrayInputStream(bao.toByteArray()), Encoding.CHARSET_UTF8));
        assertEquals(4, props.size());
        assertEquals("中文", props.get("chinese"));
        assertEquals("最棒的", props.get("nutz"));
        assertEquals("jdbc:mysql://127.0.0.1:3306/nutzbook", props.get("db.url"));
        assertEquals("jdbc:mysql://127.0.0.1:3306/nutzbook", props.get("db.url2"));
    }
}
