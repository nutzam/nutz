package org.nutz.lang.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.junit.Test;
import org.nutz.lang.Xmls;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlsTest {

    @Test
    public void testAsMap() {
        String xmlStr = "<root><name>wendal</name><age>29</age><skills><java>good</java><lua>ok</lua></skills></root>";
        Document doc = Xmls.xml(new ByteArrayInputStream(xmlStr.getBytes()));
        Element root = doc.getDocumentElement();
        Map<String, Object> map = Xmls.asMap(root);

        assertEquals("wendal", map.get("name").toString());
    }

}
