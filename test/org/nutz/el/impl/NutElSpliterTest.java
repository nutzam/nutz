package org.nutz.el.impl;

import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.nutz.el.*;

public class NutElSpliterTest {

	@Test
	public void test_3_float() {
		String exp = ".3+.2*.5";
		List<ElSymbol> items = El.split(exp);
		Iterator<ElSymbol> it = items.iterator();

		assertEquals("0.3", it.next().toString());
		assertEquals("+", it.next().toString());
		assertEquals("0.2", it.next().toString());
		assertEquals("*", it.next().toString());
		assertEquals("0.5", it.next().toString());
	}

	@Test
	public void test_args_3() {
		String exp = ",.5";
		List<ElSymbol> items = El.split(exp);
		Iterator<ElSymbol> it = items.iterator();

		// ,
		ElSymbol item = it.next();
		assertEquals(ElSymbolType.COMMA, item.getType());

		// 0.5
		item = it.next();
		assertEquals(ElSymbolType.FLOAT, item.getType());
		assertEquals("0.5", item.toString());

	}

	@Test
	public void test_special_float() {
		String exp = ".5 + a.count()";
		List<ElSymbol> items = El.split(exp);
		Iterator<ElSymbol> it = items.iterator();

		// .5
		ElSymbol item = it.next();
		assertEquals(ElSymbolType.FLOAT, item.getType());
		assertEquals("0.5", item.toString());

		// +
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("+", item.toString());

		// a
		item = it.next();
		assertEquals(ElSymbolType.VAR, item.getType());
		assertEquals("a", item.toString());

		// .
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals(".", item.toString());

		// count
		item = it.next();
		assertEquals(ElSymbolType.VAR, item.getType());
		assertEquals("count", item.toString());

		// ( 和 )
		assertEquals(ElSymbolType.LEFT_PARENTHESIS, it.next().getType());
		assertEquals(ElSymbolType.RIGHT_PARENTHESIS, it.next().getType());

	}

	@Test
	public void test_simple() {
		String exp = "1+a-3*4/5";
		List<ElSymbol> items = El.split(exp);
		Iterator<ElSymbol> it = items.iterator();

		// 1
		ElSymbol item = it.next();
		assertEquals(ElSymbolType.INT, item.getType());
		assertEquals("1", item.toString());

		// +
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("+", item.toString());

		// a
		item = it.next();
		assertEquals(ElSymbolType.VAR, item.getType());
		assertEquals("a", item.toString());

		// -
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("-", item.toString());

		// 3
		item = it.next();
		assertEquals(ElSymbolType.INT, item.getType());
		assertEquals("3", item.toString());

		// *
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("*", item.toString());

		// 4
		item = it.next();
		assertEquals(ElSymbolType.INT, item.getType());
		assertEquals("4", item.toString());

		// /
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("/", item.toString());

		// 5
		item = it.next();
		assertEquals(ElSymbolType.INT, item.getType());
		assertEquals("5", item.toString());

	}

	@Test
	public void test_complex() {
		String exp = "123 + (4.5 -(abc*true / '666')) . null";
		List<ElSymbol> items = El.split(exp);
		Iterator<ElSymbol> it = items.iterator();

		// 123
		ElSymbol item = it.next();
		assertEquals(ElSymbolType.INT, item.getType());
		assertEquals("123", item.toString());

		// +
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("+", item.toString());

		// (
		item = it.next();
		assertEquals(ElSymbolType.LEFT_PARENTHESIS, item.getType());

		// 4.5
		item = it.next();
		assertEquals(ElSymbolType.FLOAT, item.getType());
		assertEquals("4.5", item.toString());

		// -
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("-", item.toString());

		// (
		item = it.next();
		assertEquals(ElSymbolType.LEFT_PARENTHESIS, item.getType());

		// abc
		item = it.next();
		assertEquals(ElSymbolType.VAR, item.getType());
		assertEquals("abc", item.toString());

		// *
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("*", item.toString());

		// true
		item = it.next();
		assertEquals(ElSymbolType.BOOL, item.getType());
		assertEquals("true", item.toString());

		// /
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals("/", item.toString());

		// '666'
		item = it.next();
		assertEquals(ElSymbolType.STRING, item.getType());
		assertEquals("'666'", item.toString());

		// )
		item = it.next();
		assertEquals(ElSymbolType.RIGHT_PARENTHESIS, item.getType());

		// )
		item = it.next();
		assertEquals(ElSymbolType.RIGHT_PARENTHESIS, item.getType());

		// .
		item = it.next();
		assertEquals(ElSymbolType.OPT, item.getType());
		assertEquals(".", item.toString());

		// null
		item = it.next();
		assertEquals(ElSymbolType.NULL, item.getType());
		assertEquals("null", item.toString());

		assertFalse(it.hasNext());
	}

}
