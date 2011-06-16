package org.nutz.el2;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nutz.el2.speed.SimpleSpeedTest;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

public class El2Test {
	El2 el;
	
	@Before
	public void setUp(){
		el = new El2();
	}
	
	@Test
	public void notCalculateOneNumber(){
		assertEquals(1, el.eval("1"));
		assertEquals(0.1, el.eval(".1"));
		assertEquals(0.1d, el.eval("0.1"));
		assertEquals(0.1f, el.eval("0.1f"));
		assertEquals(0.1d, el.eval("0.1d"));
		assertEquals(true, el.eval("true"));
		assertEquals(false, el.eval("false"));
		assertEquals("jk", el.eval("'jk'"));
	}
	
	@Test
	public void simpleCalculate(){
		//加
		assertEquals(2, el.eval("1+1"));
		assertEquals(2.2, el.eval("1.1+1.1"));
		//减
		assertEquals(1, el.eval("2-1"));
		//乘
		assertEquals(9, el.eval("3*3"));
		assertEquals(0, el.eval("3*0"));
		//除
		assertEquals(3, el.eval("9/3"));
		assertEquals(2.2, el.eval("4.4/2"));
//		assertEquals(3.3, el.eval("9.9/3"));//哭死,这个要报错,计算结果是:3.3000000000000003
		//取余
		assertEquals(1, el.eval("5%2"));
		
		
	}
	/**
	 * 多级运算
	 */
	@Test
	public void multiStageOperation(){
		assertEquals(3, el.eval("1 + 1 + 1"));
		assertEquals(1, el.eval("1+1-1"));
		assertEquals(-1, el.eval("1-1-1"));
		assertEquals(1, el.eval("1-(1-1)"));
		assertEquals(7, el.eval("1+2*3"));
		assertEquals(2*4+2*3+4*5, el.eval("2*4+2*3+4*5"));
		assertEquals(9+8*7+(6+5)*((4-1*2+3)), el.eval("9+8*7+(6+5)*((4-1*2+3))"));
		assertEquals(.3+.2*.5,el.eval(".3+.2*.5"));
		assertEquals((.5 + 0.1)*.9, el.eval("(.5 + 0.1)*.9"));
	}
	/**
	 * 空格
	 */
	@Test
	public void sikpSpace(){
		//空格检测
		assertEquals(3, el.eval("    1 + 2    "));
	}
	/**
	 * 逻辑运算
	 */
	@Test
	public void logical(){
		assertEquals(true, el.eval("2 > 1"));
		assertEquals(false, el.eval("2 < 1"));
		assertEquals(true, el.eval("2 >= 2"));
		assertEquals(true, el.eval("2 <= 2"));
		assertEquals(true, el.eval("2 == 2 "));
		assertEquals(true, el.eval("1 != 2"));
		assertEquals(true, el.eval("!(1 == 2)"));
		assertEquals(true, el.eval("!false"));
		assertEquals(true, el.eval("true || false"));
		assertEquals(false, el.eval("true && false"));
		assertEquals(false, el.eval("false || true && false"));
	}
	
	/**
	 * 三元运算
	 * ?:
	 */
	@Test
	public void threeTernary(){
		assertEquals(2, el.eval("1>0?2:3"));
		assertEquals(2, el.eval("1>0&&1<2?2:3"));
	}
	
	/**
	 * 字符串测试
	 */
	@Test
	public void stringTest(){
		assertEquals("jk", el.eval("'jk'"));
		assertEquals(2, el.eval("'jk'.length()"));
		assertEquals(2, el.eval("\"jk\".length()"));
		assertEquals("jk", el.eval("\"    jk   \".trim()"));
		assertEquals("j\\n\\tk", el.eval("\"j\\n\\tk\""));
	}
	
	@Test
	public void test_issue_397_3() {
		int expect = 1 / 1 + 10 * (1400 - 1400) / 400;
		Object val = el.eval("1/1+10*(1400-1400)/400");
		assertEquals(expect, val);
	}
	
	/**
	 * 带负数的运算
	 */
	@Test
	public void negative(){
		assertEquals(-1, el.eval("-1"));
		assertEquals(0, el.eval("-1+1"));
		assertEquals(9+8*7+(6+5)*(-(4-1*2+3)), el.eval("9+8*7+(6+5)*(-(4-1*2+3))"));
	}
	
	/**
	 * 方法调用
	 */
	@Test
	public void callMethod(){
		assertEquals('j',el.eval("'jk'.charAt(0)"));
		assertEquals("cde", el.eval("\"abcde\".substring(2)"));
		assertEquals("b", el.eval("\"abcde\".substring(1,2)"));
		assertEquals(true, el.eval("\"abcd\".regionMatches(2,\"ccd\",1,2)"));
		assertEquals("bbbb", el.eval("'  abab  '.replace('a','b').trim()"));
	}
	
	/**
	 * 参数
	 */
	@Test
	public void test_simple_condition() {
		Context context = Lang.context();
		context.set("a", 10);
		assertEquals(10, el.eval(context, "a"));
		assertEquals(20, el.eval(context, "a + a"));
		
		context.set("b", "abc");
		assertEquals(25, el.eval(context, "a + 2 +a+ b.length()"));
		
		String s = "a>5?'GT 5':'LTE 5'";
		assertEquals("GT 5", el.eval(context, s));
		context.set("a", 5);
		assertEquals("LTE 5", el.eval(context, s));
		
		assertEquals("jk", el.eval("\"j\"+\"k\""));
		
	}
	
	@Test
	public void context(){
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		list.add("jk");
		context.set("a", list);
		assertEquals("jk", el.eval(context, "a.get((1-1))"));
		assertEquals("jk", el.eval(context, "a.get(1-1)"));
		assertEquals("jk", el.eval(context, "a.get(0)"));
		
		assertTrue((Boolean)el.eval(Lang.context(),"a==null"));
		try{
			assertTrue((Boolean)el.eval(Lang.context(), "a.a"));
			fail();
		}catch(Exception e){}
	}
	
	/**
	 * 数组测试
	 */
	@Test
	public void array(){
		Context context = Lang.context();
		String[] str = new String[]{"a","b","c"};
		String[][] bb = new String[][]{{"a","b"},{"c","d"}};
		context.set("a", str);
		context.set("b", bb);
		assertEquals("b", el.eval(context, "a[1]"));
		assertEquals("b", el.eval(context, "a[1].toString()"));
		assertEquals("b", el.eval(context, "a[2-1]"));
		assertEquals("d", el.eval(context, "b[1][1]"));
	}
	
	/**
	 * 属性测试
	 */
	@Test
	public void field(){
		class abc{
			@SuppressWarnings("unused")
			public String name = "jk";
		}
		Context context = Lang.context();
		context.set("a", new abc());
		assertEquals("jk", el.eval(context, "a.name"));
//		assertFalse((Boolean)el.eval("Boolean.FALSE"));
	}
	
	/**
	 * 自定义函数
	 */
	@Test
	public void custom(){
		assertEquals(2, el.eval("max(1, 2)"));
		assertEquals("jk", el.eval("trim('    jk    ')"));
	}
	
	@Test
	public void speed(){
		SimpleSpeedTest z = new SimpleSpeedTest();
		int num = 4988;
		String elstr = "num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7)-z.abc(i)";
		int i = 5000;
		Context con = Lang.context();
		con.set("num", num);
		con.set("i", i);
		con.set("z", z);
		assertEquals(num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7)-z.abc(i), el.eval(con, elstr));
	}
	
	@Test
	public void lssue_486(){
		assertEquals(2+(-3),el.eval("2+(-3)"));
		assertEquals(2+-3,el.eval("2+-3"));
		assertEquals(2*-3,el.eval("2*-3"));
		assertEquals(-2*-3,el.eval("-2*-3"));
		assertEquals(2/-3,el.eval("2/-3"));
		assertEquals(2%-3,el.eval("2%-3"));
	}
	
	/**
	 * map测试
	 */
	@Test
	public void map(){
		Context context = Lang.context();
		context.set("a", Lang.map("{x:10,y:50,txt:'Hello'}"));
		
		assertEquals(100, el.eval(context, "a.get('x')*10"));
		assertEquals(100, el.eval(context, "a.x*10"));
		assertEquals(100, el.eval(context, "a['x']*10"));
		assertEquals("Hello-40", el.eval(context, "a.get('txt')+(a.get('x')-a.get('y'))"));
	}
	
	/**
	 * list测试
	 */
	@Test
	public void list(){
		Context context = Lang.context();
		List<String> list = new ArrayList<String>();
		context.set("b", list);
		assertEquals(0, el.eval(context, "b.size()"));
		list.add("");
		assertEquals(1, el.eval(context, "b.size()"));
		el.eval(context, "b.add('Q\nQ')");
		assertEquals(2, el.eval(context, "b.size()"));
	}
	
	@Test
	public void complexOperation(){
		assertEquals(1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71, el.eval("1000+100.0*99-(600-3*15)%(((68-9)-3)*2-100)+10000%7*71"));
		System.out.println(6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3);
		assertEquals(6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3, el.eval("6.7-100>39.6 ? 5==5? 4+5:6-1 : !(100%3-39.0<27) ? 8*2-199: 100%3"));
		
		Context vars = Lang.context();
		vars.set("i",100);
        vars.set("pi",3.14f);
        vars.set("d",-3.9);
        vars.set("b",(byte)4);
        vars.set("bool",false);
        vars.set("t", "");
        String t = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99 ==i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
//        t = "i * pi + (d * b - 199) / (1 - d * pi) - (2 + 100 - i / pi) % 99";
        System.out.println(100*3.14f+(-3.9*(byte)4-199)/(1-(-3.9*3.14f))-(2+100-100/3.14f)%99);
        assertEquals(true, el.eval(vars, t));
	}
}
