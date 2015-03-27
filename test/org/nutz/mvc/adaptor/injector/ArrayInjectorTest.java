package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.adaptor.ParamInjector;

public class ArrayInjectorTest {

    public static ArrayInjector inj(String name, Type type, boolean auto_split) {
        return new ArrayInjector(name, null, type, null, null, auto_split);
    }

    /**
     * for issue #816
     */
    @Test
    public void test_array_no_split() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("abc", Lang.array("A,B"));

        // 执行
        ParamInjector pi = inj("abc", String[].class, false);
        Object obj = pi.get(null, req, null, null);

        // 检测
        assertEquals(String[].class, obj.getClass());

        String[] ss = (String[]) obj;
        assertEquals(1, ss.length);
        assertEquals("A,B", ss[0]);
    }

    /**
     * for issue #816
     */
    @Test
    public void test_array_auto_split() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("abc", Lang.array("A,B"));

        // 执行
        ParamInjector pi = inj("abc", String[].class, true);
        Object obj = pi.get(null, req, null, null);

        // 检测
        assertEquals(String[].class, obj.getClass());

        String[] ss = (String[]) obj;
        assertEquals(2, ss.length);
        assertEquals("A", ss[0]);
        assertEquals("B", ss[1]);
    }

    /**
     * for issue #816
     */
    @Test
    public void test_array_no_split2() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("abc", Lang.array("A,B", "X,Y"));

        // 执行
        ParamInjector pi = inj("abc", String[].class, false);
        Object obj = pi.get(null, req, null, null);

        // 检测
        assertEquals(String[].class, obj.getClass());

        String[] ss = (String[]) obj;
        assertEquals(2, ss.length);
        assertEquals("A,B", ss[0]);
        assertEquals("X,Y", ss[1]);
    }

}
