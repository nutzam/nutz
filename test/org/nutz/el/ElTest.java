package org.nutz.el;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.el.obj.BinElObj;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class ElTest {
	
	@Test
	public void test_float(){
		ElValue val = El.eval("(2-3)*0.1");
		assertEquals(Float.valueOf("-0.1"),val.getFloat());
	}
	
	@Test
	public void test_simple_and_or(){
		ElValue val = El.eval("4 && 0 || 5");
		assertTrue(val.getBoolean().booleanValue());
	}

	@Test
	public void test_semicolon() {
		ElValue val = El.eval("1+2;+99");
		assertEquals(3, val.getInteger().intValue());
	}

	@Test
	public void test_issue_397_1() {
		int expect = 1 / (1 + 10 * (1400 - 1400) / 400);
		ElValue val = El.eval("1/(1+10*(1400-1400)/400)");
		assertEquals(expect, val.getInteger().intValue());
	}

	@Test
	public void test_issue_397_2() {
		int expect = 1 / (1 + (10 * (1400 - 1400)) / 400);
		ElValue val = El.eval("1/(1+(10*(1400-1400))/400)");
		assertEquals(expect, val.getInteger().intValue());
	}

	@Test
	public void test_issue_397_3() {
		int expect = 1 / 1 + 10 * (1400 - 1400) / 400;
		ElValue val = El.eval("1/1+10*(1400-1400)/400");
		assertEquals(expect, val.getInteger().intValue());
	}

	@Test
	public void test_multi_opt_01() {
		ElValue val = El.eval("2*4+2*3+4*5");
		int exp = 2 * 4 + 2 * 3 + 4 * 5;
		assertEquals(exp, val.getInteger().intValue());
	}

	@Test
	public void test_simple_static_invoke() {
		assertEquals("abc", El.eval("' abc '.trim()").getString());
		assertEquals(3, El.eval("'123'.length()").getInteger().intValue());
		assertEquals("cde", El.eval("'abcde'.substring(2)").getString());
		assertEquals("bbbb", El.eval("'  abab  '.replace('a','b').trim()").getString());
	}

	@Test
	public void test_simple_condition() {
		String s = "a>5?'GT 5':'LTE 5'";
		BinElObj exp = El.compile(s);

		Context context = Lang.context();
		context.set("a", 10);
		assertEquals("GT 5", exp.eval(context).getString());

		context.set("a", 5);
		assertEquals("LTE 5", exp.eval(context).getString());

	}

	@Test
	public void test_array_access() {
		Context context = Lang.context();
		context.set("a", Lang.array(3, 5, 7));
		assertEquals(3, El.eval(context, "a[0]").getInteger().intValue());
		assertEquals(5, El.eval(context, "a[1]").getInteger().intValue());
		assertEquals(7, El.eval(context, "a[2]").getInteger().intValue());
		assertEquals(15, El.eval(context, "a[0]+a[1]+a[2]").getInteger().intValue());
		assertEquals(56, El.eval(context, "(a[0]+a[1])*a[2]").getInteger().intValue());
		assertEquals(3, El.eval(context, "a.length").getInteger().intValue());
	}

	@Test
	public void test_global_invoke() {
		assertEquals("abc", El.eval("trim(' abc  ')").getString());
	}

	@Test
	public void test_simple_invoke() {
		Context context = Lang.context();
		context.set("a", Lang.map("{x:10,y:50,txt:'Hello'}"));

		assertEquals(100, El.eval(context, "a.get('x')*10").getInteger().intValue());
		assertEquals(49, El.eval(context, "a.get('y')-1").getInteger().intValue());
		assertEquals("Hello-40", El.eval(context, "a.get('txt')+(a.get('x')-a.get('y'))")
									.getString());
		assertEquals("Hello", El.eval(context, "a.get('txt')").getString());
		assertEquals(3, El.eval(context, "a.size()").getInteger().intValue());

		// 测试访问符号
		assertEquals(100, El.eval(context, "a.x*10").getInteger().intValue());
		assertEquals(100, El.eval(context, "a['x']*10").getInteger().intValue());
	}

	@Test
	public void test_simple_count() {
		assertEquals((3 + 2 * 5), El.eval("3+2*5").getInteger().intValue());
		assertEquals(((3 + 2) * 5), El.eval("(3+2)*5").getInteger().intValue());

		assertEquals(Float.valueOf(.3f + .2f * .5f), El.eval(".3+.2*.5").getFloat());
		assertEquals(Float.valueOf((.5f + .1f) * .9f), El.eval("(.5 + 0.1)*.9").getFloat());
	}

	@Test
	public void test_invoke() {
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		context.set("b", list);
		assertEquals(0, El.eval(context, "b.size()").getInteger().intValue());
		list.add("");
		assertEquals(1, El.eval(context, "b.size()").getInteger().intValue());

		El.eval(context, "b.add('Q\nQ')");
		assertEquals(2, El.eval(context, "b.size()").getInteger().intValue());

	}

	@Test
	public void test_boolean() {
		assertFalse(El.eval("false").getBoolean());
		assertTrue(El.eval("(10+6)==16").getBoolean());
		assertTrue(El.eval("3>0").getBoolean());
		assertTrue(El.eval("3 >= 3").getBoolean());
		assertTrue(El.eval("3 >= 1+1").getBoolean());

		Context context = Lang.context();
		assertTrue(El.eval(context, "a == null").getBoolean());
		try {
			assertTrue(El.eval(context, "a.a").getBoolean());
			fail();
		}
		catch (ElException e) {}
		catch (Exception e) {
			fail();
		}
		try {
			assertTrue(El.eval("a.a.a").getBoolean());
		}
		catch (ElException e) {}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test_NEQ() {
		Context context = Lang.context();
		context.set("a", 3);
		assertTrue(El.eval(context, "a != null").getBoolean());
		assertTrue(El.eval("3 != 1").getBoolean());
	}

	@Test
	public void test_invoke_method_of_string() {
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		list.add("");
		context.set("b", list);
		El.eval(context, "b[0].toString()");
		El.eval(context, "b.get(0).toString()");
		El.eval(context, "b[0].equals(b[0])");
		El.eval(context, "b[0].equals('')");
	}

	@Test
	public void test_object_equal() {
		Context context = Lang.context();
		context.set("b", "abc");
		context.set("c", new Object());
		context.set("d", null);
		assertTrue(El.eval(context, "b != null").getBoolean());
		assertTrue(El.eval(context, "b == b").getBoolean());
		assertTrue(El.eval(context, "c != null").getBoolean());
		assertTrue(El.eval(context, "c == c").getBoolean());
		assertTrue(El.eval(context, "d == null").getBoolean());
		assertTrue(El.eval(context, "d == d").getBoolean());
		assertEquals("wendal", El.eval(context, "b != null ? 'wendal' : null").getString());
	}
	
	@Test
	public void test_issue_485_1() {
		assertEquals(Float.valueOf((2100 - 2000) * 0.05f), El.eval("(2100-2000)*0.05").getFloat());
		assertEquals(Float.valueOf((2100 - 2000) * .05f), El.eval("(2100-2000)*.05").getFloat());
	}
	
	@Test
	public void test_issue_485_2() {
		assertEquals(Float.valueOf(1/3*3), El.eval("1/3*3").getFloat());
		assertEquals(Float.valueOf((87-32)*5/9), El.eval("(87-32)*5/9").getFloat());
		assertEquals(Float.valueOf(35.0f/10), El.eval("35/10").getFloat());
	}
	
	@Test
	public void test_issue_485_3() {
		assertEquals(Float.valueOf(1%3), El.eval("1%3").getFloat());
		assertEquals(Float.valueOf((87-32)*5%9), El.eval("(87-32)*5%9").getFloat());
		assertEquals(Float.valueOf(35.0f%10), El.eval("35%10").getFloat());
	}
}
