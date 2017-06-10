package org.nutz.lang.tmpl;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;

public class TmplTest {

    @Test
    public void test_customized_a() {
        assertEquals("A100C", Tmpl.exec("A@<b(int)?89>C", "@", "<", ">", Lang.map("b:100"), true));
        assertEquals("A100C", Tmpl.exec("A@{b(int)?89}C", "@", Lang.map("b:100"), true));
    }

    @Test
    public void test_bracket_mode() {
        assertEquals("A100C", Tmpl.exec("A${b(int)?89}C", Lang.map("b:100")));
        assertEquals("A89C", Tmpl.exec("A${b(int)?89}C", null));
    }

    @Test
    public void test_json_format() {
        assertEquals("null", Tmpl.exec("${a<json>}", Lang.map("")));
        assertEquals("null", Tmpl.exec("${a<json>}", Lang.map("a:null")));
        assertEquals("{x:100,y:99}", Tmpl.exec("${a<json:c>}", Lang.map("a:{x:100,y:99}")));
        assertEquals("{\"x\":100,\"y\":99}",
                     Tmpl.exec("${a<json:cq>}", Lang.map("a:{x:100,y:99}")));
        assertEquals("", Tmpl.exec("${a<json>?}", Lang.map("")));
        assertEquals("[]", Tmpl.exec("${a<json>?[]}", Lang.map("")));
        assertEquals("{}", Tmpl.exec("${a<json>?-obj-}", Lang.map("")));
        assertEquals("xyz", Tmpl.exec("${a<json>?-obj-}", Lang.map("a:'xyz'")));
        assertEquals("{k:[3, true, \"a\"]}",
                     Tmpl.exec("${a<json:c>?-obj-}", Lang.map("a:{k:[3,true,'a']}")));
    }

    @Test
    public void test_string_format() {
        assertEquals("AB   C", Tmpl.exec("A${b<:%-4s>?}C", Lang.map("b:'B'}")));
        // assertEquals("AB C", Tmpl.exec("A${b<string:%-4s>?}C",
        // Lang.map("b:'B'}")));
    }

    @Test
    public void test_escape() {
        assertEquals("A${b}C", Tmpl.exec("A$${b}C", Lang.map("b:'BB'}")));
        assertEquals("${A}", Tmpl.exec("$${${x}}", Lang.map("x:'A'")));
    }

    @Test
    public void test_dynamic_dft() {
        assertEquals("ABC", Tmpl.exec("A${b?@x}C", Lang.map("x:'B'}")));
    }

    @Test
    public void test_empty_dft() {
        assertEquals("AC", Tmpl.exec("A${b?}C", Lang.map("x:'B'}")));
        assertEquals("ABC", Tmpl.exec("A${b?}C", Lang.map("b:'B'}")));
    }

    @Test
    public void test_special_key() {
        assertEquals("ABC", Tmpl.exec("A${a-b}C", Lang.map("'a-b':'B'}")));
        assertEquals("ABC", Tmpl.exec("A${'a.b'}C", Lang.map("'a.b':'B'}")));
        assertEquals("A1C", Tmpl.exec("A${pos[0].'x.x'}C", Lang.map("pos:[{'x.x':1},{'y.y':2}]}")));
        assertEquals("A2C", Tmpl.exec("A${pos[1].'y.y'}C", Lang.map("pos:[{'x.x':1},{'y.y':2}]}")));
    }

    @Test
    public void test_string() {
        assertEquals("ABC", Tmpl.exec("A${a.b}C", Lang.map("a:{b:'B'}")));
        assertEquals("ABC", Tmpl.exec("A${a.b[1]}C", Lang.map("a:{b:['A','B','C']}")));
        assertEquals("ABC", Tmpl.exec("A${a?B}C", null));
    }

    @Test
    public void test_int() {
        assertEquals("003", Tmpl.exec("${n<int:%03d>}", Lang.map("n:3")));
        assertEquals("010", Tmpl.exec("${n<int:%03X>}", Lang.map("n:16")));
    }

    @Test
    public void test_simple_float() {
        assertEquals("3.00", Tmpl.exec("${n<float>}", Lang.map("n:3")));
        assertEquals("0.98", Tmpl.exec("${n<float>?.984}", null));
    }

    @Test
    public void test_date() {
        long ms = System.currentTimeMillis();
        Date d = Times.D(ms);
        String sd = Times.format("yyyy-MM-dd'T'HH:mm:ss", d);
        assertEquals(sd, Tmpl.exec("${d<date>}", Lang.mapf("d:%s", ms)));
        assertEquals(Times.sD(d), Tmpl.exec("${d<date:yyyy-MM-dd>}", Lang.mapf("d:'%s'", sd)));
    }

    @Test
    public void test_boolean() {
        assertEquals("yes", Tmpl.exec("${v<boolean:no/yes>}", Lang.map("v:true")));
        assertEquals("no", Tmpl.exec("${v<boolean:no/yes>}", Lang.map("v:false")));
        assertEquals("no", Tmpl.exec("${v<boolean:no/yes>?false}", null));

        assertEquals("是", Tmpl.exec("${v<boolean:否/是>}", Lang.map("v:true")));
        assertEquals("否", Tmpl.exec("${v<boolean:否/是>}", Lang.map("v:false")));
        assertEquals("否", Tmpl.exec("${v<boolean:否/是>?false}", null));

        assertEquals("false", Tmpl.exec("${v<boolean>?false}", null));
        assertEquals("true", Tmpl.exec("${v<boolean>?true}", Lang.map("{}")));

        assertEquals("false", Tmpl.exec("${v<boolean>}", null));
        assertEquals("false", Tmpl.exec("${v<boolean>}", Lang.map("{}")));
    }

}
