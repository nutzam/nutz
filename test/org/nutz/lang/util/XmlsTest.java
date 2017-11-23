package org.nutz.lang.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.lang.Xmls;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlsTest extends Assert {
 
    @Test
    public void test_two_child(){
        InputStream ins = getClass().getResourceAsStream("abc.xml");
        Element element = Xmls.xml(ins).getDocumentElement();
        Element returnsms = Xmls.getEle(element, "//returnsms");
        List<Element> statusboxList = Xmls.children(returnsms);
        for (int i = 0; i < statusboxList.size(); i++) {
            Element statusbox = statusboxList.get(i);
            assertEquals(""+(i+1), statusbox.getAttribute("id"));
            Element ele = Xmls.getEle(statusbox, "./mobile");
            assertEquals(""+(i+1)+"-1", ele.getAttribute("id"));
            System.out.println(Xmls.getText(ele));
        }
        List<Element> mobiles = Xmls.getEles(element, "//mobile");
        assertEquals(2, mobiles.size());
        assertEquals("18600321144", mobiles.get(1).getTextContent());
    }
    
    @Test
    public void testAsMap() {
        String xmlStr = "<root><name>wendal</name><age>29</age><skills><java>good</java><lua>ok</lua></skills></root>";
        Document doc = Xmls.xml(new ByteArrayInputStream(xmlStr.getBytes()));
        Element root = doc.getDocumentElement();
        Map<String, Object> map = Xmls.asMap(root);

        assertEquals("wendal", map.get("name").toString());
    }
    
    @Test
    public void test_dup_as_list() {
        String xmlStr = "<root><user><pet><name>wendal</name></pet><pet><name>zozoh</name></pet></user></root>";
        Document doc = Xmls.xml(new ByteArrayInputStream(xmlStr.getBytes()));
        Element root = doc.getDocumentElement();
        NutMap map = Xmls.asMap(root, true, true);
        System.out.println(map);
        NutMap user = map.getAs("user", NutMap.class);
        Object pets = user.get("pet");
        assertTrue(pets instanceof Collection || pets.getClass().isArray());
    }
}
