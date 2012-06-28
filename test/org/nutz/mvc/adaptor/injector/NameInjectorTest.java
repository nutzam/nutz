package org.nutz.mvc.adaptor.injector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;

public class NameInjectorTest {

    @SuppressWarnings({"unchecked"})
    private static <T> T inj(String name, Class<T> type, HttpServletRequest req) {
        return (T) new NameInjector(name, type, null).get(null, req, null, null);
    }

    /**
     * 根据 Issue 272，如果为空串，原生类型的外覆类应该返回 null
     */
    @Test
    public void test_balnk_param_to_number() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("a", "  ");
        req.setParameter("b", "  ");

        // 执行 & 检测
        assertNull(inj("a", Long.class, req));
        assertEquals(0, (int) inj("b", int.class, req));
    }

    @Test
    public void test_duplicate_name() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameterValues("abc", Lang.array("1", "2", "3"));

        // 执行 & 检测
        String[] ss = inj("abc", String[].class, req);
        assertEquals(3, ss.length);
        assertEquals("1", ss[0]);
        assertEquals("2", ss[1]);
        assertEquals("3", ss[2]);
    }

    @Test
    public void test_normal_int() {
        // 准备数据
        MockHttpServletRequest req = Mock.servlet.request();
        req.setParameter("abc", "1");

        // 执行 & 检测
        assertEquals(Integer.valueOf(1), inj("abc", int.class, req));
    }
}
