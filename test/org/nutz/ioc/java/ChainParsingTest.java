package org.nutz.ioc.java;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.impl.ScopeContext;

public class ChainParsingTest {

    private static String NAME(String name) {
        return "org.nutz.ioc.java.TFunc." + name;
    }

    private static ChainNode N(String s) {
        return new ChainParsing(s).getNode();
    }

    @Test(expected = RuntimeException.class)
    public void test_nostatic_function() {
        N(NAME("noStatic"));
    }

    @Test(expected = RuntimeException.class)
    public void test_unexists_function() {
        N(NAME("unexists"));
    }

    @Test
    public void test_parse_returnvalue_invoke() {
        String s = "$a.x().f('m', false)";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
    }
    
    @Test
    public void test_parse_attr_chain() {
        String s = "$a.b.c()";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
    }

    @Test
    public void test_invoke_with_number() {
        String s = NAME("dup('ABC',0)");
        ChainNode cn = N(s);
        assertEquals("", cn.eval(null));

        s = NAME("dup('ABC',3)");
        cn = N(s);
        assertEquals("ABCABCABC", cn.eval(null));

        s = NAME("dup('ABC',-2)");
        cn = N(s);
        assertEquals("A", cn.eval(null));

        s = NAME("tFloat('ABC', .5f)");
        cn = N(s);
        assertEquals("ABC:0.5", cn.eval(null));
    }

    @Test
    public void test_normal_static_call() {
        String s = NAME("getAbc()");
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
        assertEquals("ABC", cn.eval(null));

        s = NAME("getAbc");
        cn = N(s);
        assertEquals(NAME("getAbc()"), cn.toString());
        assertEquals("ABC", cn.eval(null));

        String s2 = NAME("abc");
        cn = N(s2);
        assertEquals(NAME("getAbc()"), cn.toString());
        assertEquals("ABC", cn.eval(null));
    }

    @Test
    public void test_with_arguments() {
        String s = NAME("checkCase (true, 'aBc' )");
        ChainNode cn = N(s);
        assertEquals(NAME("checkCase(true, 'aBc')"), cn.toString());
        assertEquals("ABC", cn.eval(null));

        s = NAME("checkCase (false, 'aBc' )");
        cn = N(s);
        assertEquals("abc", cn.eval(null));
    }

    @Test
    public void test_constants_ioc() {
        String s = "@Ioc.get(null, 'xyz')";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
    }

    @Test
    public void test_constants_name() {
        String s = "@Name.substring(0, 6)";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
        IocMaking ing = new IocMaking(null, null, null, null, null, "123456789");
        assertEquals("123456", cn.eval(ing));
    }

    @Test
    public void test_constants_context() {
        String s = "@Context.save('xx', 'tt', null)";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
        IocMaking ing = new IocMaking(null,
                                      null,
                                      new ScopeContext("app"),
                                      null,
                                      null,
                                      null);
        assertFalse((Boolean) cn.eval(ing));
    }

    @Test
    public void test_normal_ioc_object() {
        String s = "$obj.xyz()";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
    }

    @Test
    public void test_normal_ioc_object_with_args() {
        String s = "$obj.xyz($tt, @Ioc, true, 34, 'TbT')";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
    }

    @Test
    public void test_static_field() {
        N(NAME("XNAME"));
    }

    @Test
    public void deep_args() {
        String s = "$obj.xyz($tt.zz(), @Ioc, true, 34, 'TbT')";
        ChainNode cn = N(s);
        assertEquals(s, cn.toString());
        s = "$obj.xyz(@Ioc, true, $tt.zz.nn, 34, 'TbT')";
        cn = N(s);
        assertEquals(s, cn.toString());

        s = "$obj.xyz(@Ioc, true, 34, 'TbT', $tt.zz.nn())";
        cn = N(s);
        assertEquals(s, cn.toString());

        s = "$obj.xyz(@Ioc.bbb)";
        cn = N(s);
        assertEquals(s, cn.toString());

        //谁的配置会写成这样，呵呵
        s = "$obj.xyz($bb.cc($dd.ee(true)))";
        cn = N(s);
        assertEquals(s, cn.toString());
    }
}
