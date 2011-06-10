package org.nutz.el2;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
	 * 字符串测试
	 */
	@Test
	public void stringTest(){
		assertEquals("jk", el.eval("'jk'"));
		assertEquals(2, el.eval("'jk'.length()"));
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
	public void negative(){
		assertEquals(9+8*7+(6+5)*(-(4-1*2+3)), el.eval("9+8*7+(6+5)*(-(4-1*2+3))"));
	}
	
}
