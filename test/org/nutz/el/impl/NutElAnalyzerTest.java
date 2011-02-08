package org.nutz.el.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.el.obj.BinObj;

public class NutElAnalyzerTest {

	@Test
	public void test_invoke_and_count() {
		String exp = "a.get('x')*10";
		BinObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("10", bin.getRight().toString());

		BinObj ob = (BinObj) bin.getLeft();
		assertEquals("$a", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get', 'x']", ob.getRight().toString());
	}

	@Test
	public void test_complex() {
		String exp = "(6+map.get(.534))/178l";
		BinObj bin = El.compile(exp);

		assertEquals("/", bin.getOperator().toString());
		assertEquals("178L", bin.getRight().toString());

		BinObj ob = (BinObj) bin.getLeft();
		assertEquals("6", ob.getLeft().toString());
		assertEquals("+", ob.getOperator().toString());

		ob = (BinObj) ob.getRight();
		assertEquals("$map", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get', 0.534]", ob.getRight().toString());
	}

	@Test
	public void test_simple_invoke_at_right() {
		String exp = "8+map.get()";
		BinObj bin = El.compile(exp);

		assertEquals("8", bin.getLeft().toString());
		assertEquals("+", bin.getOperator().toString());

		BinObj ob = (BinObj) bin.getRight();
		assertEquals("$map", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get']", ob.getRight().toString());
	}

	@Test
	public void test_simple_invoke() {
		String exp = "map.get()";
		BinObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get']", bin.getRight().toString());
	}

	@Test
	public void test_simple_invoke_param1() {
		String exp = "map.get('name')";
		BinObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get', 'name']", bin.getRight().toString());
	}

	@Test
	public void test_simple_invoke_param3() {
		String exp = "map.get('name' ,  true,.5)";
		BinObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get', 'name', true, 0.5]", bin.getRight().toString());
	}

	@Test
	public void test_2_level_group() {
		String exp = "(a+(b-10))*c";
		BinObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("$c", bin.getRight().toString());

		BinObj ob = (BinObj) bin.getLeft();
		assertEquals("$a", ob.getLeft().toString());
		assertEquals("+", ob.getOperator().toString());

		ob = (BinObj) ob.getRight();
		assertEquals("$b", ob.getLeft().toString());
		assertEquals("-", ob.getOperator().toString());
		assertEquals("10", ob.getRight().toString());
	}

	@Test
	public void test_3_normal_elobj() {
		String exp = "2+3-4";
		BinObj bin = El.compile(exp);

		assertEquals("-", bin.getOperator().toString());
		assertEquals("4", bin.getRight().toString());

		BinObj lb = (BinObj) bin.getLeft();
		assertEquals("2", lb.getLeft().toString());
		assertEquals("+", lb.getOperator().toString());
		assertEquals("3", lb.getRight().toString());
	}

	@Test
	public void test_3_elobj() {
		String exp = "2+3*4";
		BinObj bin = El.compile(exp);

		assertEquals("+", bin.getOperator().toString());
		assertEquals("2", bin.getLeft().toString());

		BinObj rn = (BinObj) bin.getRight();
		assertEquals("3", rn.getLeft().toString());
		assertEquals("*", rn.getOperator().toString());
		assertEquals("4", rn.getRight().toString());
	}

	@Test
	public void test_3_elobj_with_group() {
		String exp = "(2+3)*4";
		BinObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("4", bin.getRight().toString());

		BinObj rn = (BinObj) bin.getLeft();
		assertEquals("2", rn.getLeft().toString());
		assertEquals("+", rn.getOperator().toString());
		assertEquals("3", rn.getRight().toString());
	}

}
