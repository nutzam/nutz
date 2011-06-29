package org.nutz.el;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nutz.el.El;
import org.nutz.el.speed.SimpleSpeedTest;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class El2Test {
	El el;

	@Before
	public void setUp() {
		el = new El();
	}

	@Test
	public void notCalculateOneNumber() {
		assertEquals(1, El.eval("1"));
		assertEquals(0.1, El.eval(".1"));
		assertEquals(0.1d, El.eval("0.1"));
		assertEquals(0.1f, El.eval("0.1f"));
		assertEquals(0.1d, El.eval("0.1d"));
		assertEquals(true, El.eval("true"));
		assertEquals(false, El.eval("false"));
		assertEquals("jk", El.eval("'jk'"));
	}

	@Test
	public void simpleCalculate() {
		// 加
		assertEquals(2, El.eval("1+1"));
		assertEquals(2.2, El.eval("1.1+1.1"));
		// 减
		assertEquals(1, El.eval("2-1"));
		// 乘
		assertEquals(9, El.eval("3*3"));
		assertEquals(0, El.eval("3*0"));
		// 除
		assertEquals(3, El.eval("9/3"));
		assertEquals(2.2, El.eval("4.4/2"));
		assertEquals(9.9 / 3, El.eval("9.9/3"));
		// 取余
		assertEquals(1, El.eval("5%2"));
		assertEquals(1.0 % 0.1, El.eval("1.0%0.1"));

	}

	/**
	 * 位运算
	 */
	@Test
	public void bit() {
		assertEquals(-40, El.eval("-5<<3"));
		assertEquals(-1, El.eval("-5>>3"));
		assertEquals(5, El.eval("5>>>32"));
		assertEquals(-5, El.eval("-5>>>32"));
		assertEquals(1, El.eval("5&3"));
		assertEquals(7, El.eval("5|3"));
		assertEquals(-6, El.eval("~5"));
		assertEquals(6, El.eval("5^3"));
	}

	/**
	 * 多级运算
	 */
	@Test
	public void multiStageOperation() {
		assertEquals(3, El.eval("1 + 1 + 1"));
		assertEquals(1, El.eval("1+1-1"));
		assertEquals(-1, El.eval("1-1-1"));
		assertEquals(1, El.eval("1-(1-1)"));
		assertEquals(7, El.eval("1+2*3"));
		assertEquals(2 * 4 + 2 * 3 + 4 * 5, El.eval("2*4+2*3+4*5"));
		assertEquals(9 + 8 * 7 + (6 + 5) * ((4 - 1 * 2 + 3)), El.eval("9+8*7+(6+5)*((4-1*2+3))"));
		assertEquals(.3 + .2 * .5, El.eval(".3+.2*.5"));
		assertEquals((.5 + 0.1) * .9, El.eval("(.5 + 0.1)*.9"));
	}

	/**
	 * 空格
	 */
	@Test
	public void sikpSpace() {
		// 空格检测
		assertEquals(3, El.eval("    1 + 2    "));
	}

	@Test
	public void testNull() {
		assertEquals(null, El.eval("null"));
		assertTrue((Boolean) El.eval("null == null"));
	}

	/**
	 * 逻辑运算
	 */
	@Test
	public void logical() {
		assertEquals(true, El.eval("2 > 1"));
		assertEquals(false, El.eval("2 < 1"));
		assertEquals(true, El.eval("2 >= 2"));
		assertEquals(true, El.eval("2 <= 2"));
		assertEquals(true, El.eval("2 == 2 "));
		assertEquals(true, El.eval("1 != 2"));
		assertEquals(true, El.eval("!(1 == 2)"));
		assertEquals(true, El.eval("!false"));
		assertEquals(true, El.eval("true || false"));
		assertEquals(false, El.eval("true && false"));
		assertEquals(false, El.eval("false || true && false"));
	}

	/**
	 * 三元运算 ?:
	 */
	@Test
	public void threeTernary() {
		assertEquals(2, El.eval("1>0?2:3"));
		assertEquals(2, El.eval("1>0&&1<2?2:3"));
	}

	/**
	 * 字符串测试
	 */
	@Test
	public void stringTest() {
		assertEquals("jk", El.eval("'jk'"));
		assertEquals(2, El.eval("'jk'.length()"));
		assertEquals(2, El.eval("\"jk\".length()"));
		assertEquals("jk", El.eval("\"    jk   \".trim()"));
		assertEquals("j\\n\\tk", El.eval("\"j\\n\\tk\""));
	}

	@Test
	public void test_issue_397_3() {
		int expect = 1 / 1 + 10 * (1400 - 1400) / 400;
		Object val = El.eval("1/1+10*(1400-1400)/400");
		assertEquals(expect, val);
	}

	/**
	 * 带负数的运算
	 */
	@Test
	public void negative() {
		assertEquals(-1, El.eval("-1"));
		assertEquals(0, El.eval("-1+1"));
		assertEquals(-1 - -1, El.eval("-1 - -1"));
		assertEquals(9 + 8 * 7 + (6 + 5) * (-(4 - 1 * 2 + 3)), El.eval("9+8*7+(6+5)*(-(4-1*2+3))"));
	}

	/**
	 * 方法调用
	 */
	@Test
	public void callMethod() {
		assertEquals('j', El.eval("'jk'.charAt(0)"));
		assertEquals("cde", El.eval("\"abcde\".substring(2)"));
		assertEquals("b", El.eval("\"abcde\".substring(1,2)"));
		assertEquals(true, El.eval("\"abcd\".regionMatches(2,\"ccd\",1,2)"));
		assertEquals("bbbb", El.eval("'  abab  '.replace('a','b').trim()"));
	}

	/**
	 * 参数
	 */
	@Test
	public void test_simple_condition() {
		Context context = Lang.context();
		context.set("a", 10);
		assertEquals(10, El.eval(context, "a"));
		assertEquals(20, El.eval(context, "a + a"));

		context.set("b", "abc");
		assertEquals(25, El.eval(context, "a + 2 +a+ b.length()"));

		String s = "a>5?'GT 5':'LTE 5'";
		assertEquals("GT 5", El.eval(context, s));
		context.set("a", 5);
		assertEquals("LTE 5", El.eval(context, s));

		assertEquals("jk", El.eval("\"j\"+\"k\""));

	}

	@Test
	public void context() {
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		list.add("jk");
		context.set("a", list);
		assertEquals("jk", El.eval(context, "a.get((1-1))"));
		assertEquals("jk", El.eval(context, "a.get(1-1)"));
		assertEquals("jk", El.eval(context, "a.get(0)"));

		assertTrue((Boolean) El.eval(Lang.context(), "a==null"));
		try {
			assertTrue((Boolean) El.eval(Lang.context(), "a.a"));
			fail();
		}
		catch (Exception e) {}
	}

	/**
	 * 数组测试
	 */
	@Test
	public void array() {
		Context context = Lang.context();
		String[] str = new String[]{"a", "b", "c"};
		String[][] bb = new String[][]{{"a", "b"}, {"c", "d"}};
		context.set("a", str);
		context.set("b", bb);
		assertEquals("b", El.eval(context, "a[1]"));
		assertEquals("b", El.eval(context, "a[1].toString()"));
		assertEquals("b", El.eval(context, "a[2-1]"));
		assertEquals("d", El.eval(context, "b[1][1]"));
	}

	/**
	 * 属性测试
	 */
	@Test
	public void field() {
		class abc {
			@SuppressWarnings("unused")
			public String name = "jk";
		}
		Context context = Lang.context();
		context.set("a", new abc());
		assertEquals("jk", El.eval(context, "a.name"));
		// 这个功能放弃
		// assertFalse((Boolean)El.eval("java.lang.Boolean.FALSE"));
		// assertFalse((Boolean)El.eval("Boolean.FALSE"));
	}

	/**
	 * 自定义函数
	 */
	@Test
	public void custom() {
		assertEquals(2, El.eval("max(1, 2)"));
		assertEquals("jk", El.eval("trim('    jk    ')"));
	}

	@Test
	public void speed() {
		SimpleSpeedTest z = new SimpleSpeedTest();
		int num = 4988;
		String elstr = "num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7)-z.abc(i)";
		int i = 5000;
		Context con = Lang.context();
		con.set("num", num);
		con.set("i", i);
		con.set("z", z);
		assertEquals(num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7) - z.abc(i), El.eval(con, elstr));
	}

	@Test
	public void lssue_486() {
		assertEquals(2 + (-3), El.eval("2+(-3)"));
		assertEquals(2 + -3, El.eval("2+-3"));
		assertEquals(2 * -3, El.eval("2*-3"));
		assertEquals(-2 * -3, El.eval("-2*-3"));
		assertEquals(2 / -3, El.eval("2/-3"));
		assertEquals(2 % -3, El.eval("2%-3"));
	}

	/**
	 * map测试
	 */
	@Test
	public void map() {
		Context context = Lang.context();
		context.set("a", Lang.map("{x:10,y:50,txt:'Hello'}"));

		assertEquals(100, El.eval(context, "a.get('x')*10"));
		assertEquals(100, El.eval(context, "a.x*10"));
		assertEquals(100, El.eval(context, "a['x']*10"));
		assertEquals("Hello-40", El.eval(context, "a.get('txt')+(a.get('x')-a.get('y'))"));
	}

	/**
	 * list测试
	 */
	@Test
	public void list() {
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		context.set("b", list);
		assertEquals(0, El.eval(context, "b.size()"));
		list.add("");
		assertEquals(1, El.eval(context, "b.size()"));
		El.eval(context, "b.add('Q\nQ')");
		assertEquals(2, El.eval(context, "b.size()"));
	}

	@SuppressWarnings("unused")
	@Test
	public void complexOperation() {
		assertEquals(1000
						+ 100.0
						* 99
						- (600 - 3 * 15)
						% (((68 - 9) - 3) * 2 - 100)
						+ 10000
						% 7
						* 71, El.eval("1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71"));
		assertEquals(	6.7 - 100 > 39.6 ? true ? 4 + 5 : 6 - 1
										: !(100 % 3 - 39.0 < 27) ? 8 * 2 - 199 : 100 % 3,
						El.eval("6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3"));

		Context vars = Lang.context();
		vars.set("i", 100);
		vars.set("pi", 3.14f);
		vars.set("d", -3.9);
		vars.set("b", (byte) 4);
		vars.set("bool", false);
		vars.set("t", "");
		String t = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
		// t =
		// "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
		assertEquals(true, El.eval(vars, t));

		// assertEquals('A' == ('A') || 'B' == 'B' && "ABCD" == "" && 'A' ==
		// 'A', el.eval(vars,
		// "'A' == 'A' || 'B' == 'B' && 'ABCD' == t &&  'A' == 'A'"));
		assertEquals(	true || true && false && true,
						El.eval(vars, "'A' == 'A' || 'B' == 'B' && 'ABCD' == t &&  'A' == 'A'"));
	}
}
