package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutType;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;

/**
 * 
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ObjectNavlPairInjectorTest {

    public static ObjectNavlPairInjector inj() {
        return new ObjectNavlPairInjector("pojo", MvcTestPojo.class);
    }
    
    public static ObjectNavlPairInjector inj(String prefix, Type type){
        return new ObjectNavlPairInjector(prefix, type);
    }

    /**
     * 根据 Issue 272，如果为空串，原生类型的外覆类应该返回 null
     */
    @Test
    public void test_balnk_param_to_number() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.longValue", "  ");
        req.setParameter("pojo.num", "  ");

        // 执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

        // 检测
        assertNull(pojo.longValue);
        assertEquals(0, pojo.num);
    }

    /**
     * 这个测试将检验在 HTTP 请求中，如果存在多个参数同名的情况，本注入器能否正确处理
     */
    @Test
    public void test_duplicated_name_params() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.num", 23);
        req.setParameterValues("pojo.names", Lang.array("A", "B", "C"));

        // 执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

        // 检测
        assertNull(pojo.longValue);
        assertEquals(23, pojo.num);
        assertEquals(3, pojo.names.length);
        assertEquals("A", pojo.names[0]);
        assertEquals("B", pojo.names[1]);
        assertEquals("C", pojo.names[2]);
    }

    @Test
    public void test_array_to_string() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("pojo.str", Lang.array("A", "B", "C"));

        // 执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

        // 检测
        assertEquals("A,B,C", pojo.str);
    }

    @Test
    public void test_string_to_string() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("pojo.str", Lang.array("A"));

        // 执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);

        // 检测
        assertEquals("A", pojo.str);
    }
    
    @Test
    public void test_date(){
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.date", "2010-01-01");
        // 执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(sdf.format(pojo.date));
        assertEquals("2010-01-01", sdf.format(pojo.date));
        
        req.setParameter("pojo.date", "");
        // 执行
        MvcTestPojo pojoNull = (MvcTestPojo) inj().get(null, req, null, null);
        
        assertEquals(null, pojoNull.date);
    }
    
    @Test
    public void testList(){
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.books[1]", "a");
        req.setParameter("pojo.books[ads]", "b");
        req.setParameter("pojo.books[3]", "c");
       
        //执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
        
        assertTrue(pojo.books.contains("a"));
        assertTrue(pojo.books.contains("b"));
        assertTrue(pojo.books.contains("c"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testListParam() throws NoSuchFieldException{
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("lists[1].str", "a");
        ObjectNavlPairInjector onpi = inj("lists", NutType.list(MvcTestPojo.class));
        //执行
        List<MvcTestPojo> pojo =  (List<MvcTestPojo>) onpi.get(null, req, null, null);
        assertTrue(pojo.get(0).str.contains("a"));
    }
    
    @Test
    public void testMap(){
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.maps(abc).str", "a");
        req.setParameter("pojo.maps(1).str", "b");
        req.setParameter("pojo.maps(jk).str", "c");
        req.setParameter("pojo.maps(jk).maps.nutz.str", "k");
        //执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
        
        assertEquals(pojo.maps.get("abc").str, "a");
        assertEquals(pojo.maps.get("1").str, "b");
        assertEquals(pojo.maps.get("jk").str, "c");
        assertEquals(pojo.maps.get("jk").maps.get("nutz").str, "k");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testMapParam() throws NoSuchFieldException{
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("maps(abc).str", "a");
        req.setParameter("maps(1).str", "b");
        req.setParameter("maps(jk).str", "c");
        req.setParameter("maps.jk.maps.nutz.str", "k");
        //执行
        ObjectNavlPairInjector onpi = inj("maps", NutType.map(String.class, MvcTestPojo.class));
        Map<String, MvcTestPojo> pojo = (Map<String, MvcTestPojo>) onpi.get(null, req, null, null);
        
        assertEquals(pojo.get("abc").str, "a");
        assertEquals(pojo.get("1").str, "b");
        assertEquals(pojo.get("jk").str, "c");
        assertEquals(pojo.get("jk").maps.get("nutz").str, "k");
    }
    
    @Test
    public void testSet(){
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("pojo.sets[jk].str", "c");
        req.setParameter("pojo.sets[jk].maps(nutz).str", "k");
        //执行
        MvcTestPojo pojo = (MvcTestPojo) inj().get(null, req, null, null);
        
        for(MvcTestPojo m : pojo.sets){
            assertEquals(m.str, "c");
            assertEquals(m.maps.get("nutz").str, "k");
        }
    }
    
    @Test
    public void testArray() throws NoSuchFieldException{
        //准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("arrays[1].str", "a");
        ObjectNavlPairInjector onpi = inj("arrays", NutType.array(MvcTestPojo.class));
        //执行
        MvcTestPojo[] pojo =  (MvcTestPojo[]) onpi.get(null, req, null, null);
        assertTrue(pojo[0].str.contains("a"));
    }

}
