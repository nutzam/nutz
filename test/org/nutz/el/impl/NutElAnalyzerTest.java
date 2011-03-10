package org.nutz.el.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.el.El;
import org.nutz.el.obj.BinElObj;
import org.nutz.el.obj.ConditionalElObj;
import org.nutz.el.obj.StaticElObj;

public class NutElAnalyzerTest {
	
	@Test
	public void test_simple_or_and() {
		String exp = "a || b && c || d";
		BinElObj root = El.compile(exp);

		assertEquals("$d", root.getRight().toString());
		assertEquals("||", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getLeft();
		assertEquals("$a", bin.getLeft().toString());
		assertEquals("||", bin.getOperator().toString());

		bin = (BinElObj) bin.getRight();
		assertEquals("$b", bin.getLeft().toString());
		assertEquals("&&", bin.getOperator().toString());
		assertEquals("$c", bin.getRight().toString());
	}

	@Test
	public void test_simple_or_and_with_parenthesis() {
		String exp = "a || (b && c) || d";
		BinElObj root = El.compile(exp);

		assertEquals("$d", root.getRight().toString());
		assertEquals("||", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getLeft();
		assertEquals("$a", bin.getLeft().toString());
		assertEquals("||", bin.getOperator().toString());

		bin = (BinElObj) bin.getRight();
		assertEquals("$b", bin.getLeft().toString());
		assertEquals("&&", bin.getOperator().toString());
		assertEquals("$c", bin.getRight().toString());
	}

	@Test
	public void test_simple_or() {
		String exp = "a || b || c || d";
		BinElObj root = El.compile(exp);

		assertEquals("$a", root.getLeft().toString());
		assertEquals("||", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getRight();
		assertEquals("$b", bin.getLeft().toString());
		assertEquals("||", bin.getOperator().toString());

		bin = (BinElObj) bin.getRight();
		assertEquals("$c", bin.getLeft().toString());
		assertEquals("||", bin.getOperator().toString());
		assertEquals("$d", bin.getRight().toString());
	}

	@Test
	public void test_simple_and() {
		String exp = "a && b && c && d";
		BinElObj root = El.compile(exp);

		assertEquals("$a", root.getLeft().toString());
		assertEquals("&&", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getRight();
		assertEquals("$b", bin.getLeft().toString());
		assertEquals("&&", bin.getOperator().toString());

		bin = (BinElObj) bin.getRight();
		assertEquals("$c", bin.getLeft().toString());
		assertEquals("&&", bin.getOperator().toString());
		assertEquals("$d", bin.getRight().toString());
	}

	@Test
	public void test_simple_exp() {
		String exp = "1+10*0/400";
		BinElObj root = El.compile(exp);

		assertEquals("1", root.getLeft().toString());
		assertEquals("+", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getRight();
		assertEquals("400", bin.getRight().toString());
		assertEquals("/", bin.getOperator().toString());

		bin = (BinElObj) bin.getLeft();
		assertEquals("10", bin.getLeft().toString());
		assertEquals("*", bin.getOperator().toString());
		assertEquals("0", bin.getRight().toString());
	}

	@Test
	public void test_multi_bracket() {
		String exp = "1+(10*(1400-1400))/400";
		BinElObj root = El.compile(exp);

		assertEquals("1", root.getLeft().toString());
		assertEquals("+", root.getOperator().toString());

		BinElObj bin = (BinElObj) root.getRight();
		assertEquals("400", bin.getRight().toString());
		assertEquals("/", bin.getOperator().toString());

		bin = (BinElObj) bin.getLeft();
		assertEquals("10", bin.getLeft().toString());
		assertEquals("*", bin.getOperator().toString());

		bin = (BinElObj) bin.getRight();
		assertEquals("1400", bin.getLeft().toString());
		assertEquals("-", bin.getOperator().toString());
		assertEquals("1400", bin.getRight().toString());
	}

	@Test
	public void test_string_invoke() {
		String exp = "'abc'.trim()";
		BinElObj root = El.compile(exp);

		assertEquals("'abc'", root.getLeft().toString());
		assertEquals("&invoke", root.getOperator().toString());
		assertEquals("['trim']", root.getRight().toString());
	}

	@Test
	public void test_cnd_invoke() {
		String exp = "(a>=7?b:c).count(23)";
		BinElObj root = El.compile(exp);

		/*
		 * a>=7 ? b : c
		 */
		ConditionalElObj cnd = (ConditionalElObj) root.getLeft();

		BinElObj bin = (BinElObj) cnd.getTest();
		assertEquals("$a", bin.getLeft().toString());
		assertEquals(">=", bin.getOperator().toString());
		assertEquals("7", bin.getRight().toString());

		assertEquals("$b", cnd.getTrueObj().toString());
		assertEquals("$c", cnd.getFalseObj().toString());

		/*
		 * .count(23)
		 */
		assertEquals("&invoke", root.getOperator().toString());
		assertEquals("['count', 23]", root.getRight().toString());
	}

	@Test
	public void test_simple_conditional() {
		String exp = "a?3+4:true";
		BinElObj bin = El.compile(exp);

		ConditionalElObj obj = (ConditionalElObj) bin.unwrap();

		assertEquals("$a", obj.getTest().toString());
		assertEquals(7, obj.getTrueObj().eval(null).getInteger().intValue());
		assertTrue(obj.getFalseObj() instanceof StaticElObj);
	}

	@Test
	public void test_simple_index_access() {
		String exp = "a[0]";
		BinElObj bin = El.compile(exp);

		assertEquals("$a", bin.getLeft().toString());
		assertEquals(".", bin.getOperator().toString());
		assertEquals("0", bin.getRight().toString());
	}

	@Test
	public void test_access_and_invoke() {
		String exp = "a[0].xyz()";
		BinElObj bin = El.compile(exp);

		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['xyz']", bin.getRight().toString());

		bin = (BinElObj) bin.getLeft();
		assertEquals("$a", bin.getLeft().toString());
		assertEquals(".", bin.getOperator().toString());
		assertEquals("0", bin.getRight().toString());
	}

	@Test
	public void test_invoke_and_count() {
		String exp = "a.get('x')*10";
		BinElObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("10", bin.getRight().toString());

		BinElObj ob = (BinElObj) bin.getLeft();
		assertEquals("$a", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get', 'x']", ob.getRight().toString());
	}

	@Test
	public void test_complex() {
		String exp = "(6+map.get(.534))/178l";
		BinElObj bin = El.compile(exp);

		assertEquals("/", bin.getOperator().toString());
		assertEquals("178L", bin.getRight().toString());

		BinElObj ob = (BinElObj) bin.getLeft();
		assertEquals("6", ob.getLeft().toString());
		assertEquals("+", ob.getOperator().toString());

		ob = (BinElObj) ob.getRight();
		assertEquals("$map", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get', 0.534]", ob.getRight().toString());
	}

	@Test
	public void test_simple_invoke_at_right() {
		String exp = "8+map.get()";
		BinElObj bin = El.compile(exp);

		assertEquals("8", bin.getLeft().toString());
		assertEquals("+", bin.getOperator().toString());

		BinElObj ob = (BinElObj) bin.getRight();
		assertEquals("$map", ob.getLeft().toString());
		assertEquals("&invoke", ob.getOperator().toString());
		assertEquals("['get']", ob.getRight().toString());
	}

	@Test
	public void test_simple_invoke() {
		String exp = "map.get()";
		BinElObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get']", bin.getRight().toString());
	}

	@Test
	public void test_simple_invoke_param1() {
		String exp = "map.get('name')";
		BinElObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get', 'name']", bin.getRight().toString());
	}

	@Test
	public void test_simple_invoke_param3() {
		String exp = "map.get('name' ,  true,.5)";
		BinElObj bin = El.compile(exp);

		assertEquals("$map", bin.getLeft().toString());
		assertEquals("&invoke", bin.getOperator().toString());
		assertEquals("['get', 'name', true, 0.5]", bin.getRight().toString());
	}

	@Test
	public void test_2_level_group() {
		String exp = "(a+(b-10))*c";
		BinElObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("$c", bin.getRight().toString());

		BinElObj ob = (BinElObj) bin.getLeft();
		assertEquals("$a", ob.getLeft().toString());
		assertEquals("+", ob.getOperator().toString());

		ob = (BinElObj) ob.getRight();
		assertEquals("$b", ob.getLeft().toString());
		assertEquals("-", ob.getOperator().toString());
		assertEquals("10", ob.getRight().toString());
	}

	@Test
	public void test_3_normal_elobj() {
		String exp = "2+3-4";
		BinElObj bin = El.compile(exp);

		assertEquals("-", bin.getOperator().toString());
		assertEquals("4", bin.getRight().toString());

		BinElObj lb = (BinElObj) bin.getLeft();
		assertEquals("2", lb.getLeft().toString());
		assertEquals("+", lb.getOperator().toString());
		assertEquals("3", lb.getRight().toString());
	}

	@Test
	public void test_3_elobj() {
		String exp = "2+3*4";
		BinElObj bin = El.compile(exp);

		assertEquals("+", bin.getOperator().toString());
		assertEquals("2", bin.getLeft().toString());

		BinElObj rn = (BinElObj) bin.getRight();
		assertEquals("3", rn.getLeft().toString());
		assertEquals("*", rn.getOperator().toString());
		assertEquals("4", rn.getRight().toString());
	}

	@Test
	public void test_3_elobj_with_group() {
		String exp = "(2+3)*4";
		BinElObj bin = El.compile(exp);

		assertEquals("*", bin.getOperator().toString());
		assertEquals("4", bin.getRight().toString());

		BinElObj rn = (BinElObj) bin.getLeft();
		assertEquals("2", rn.getLeft().toString());
		assertEquals("+", rn.getOperator().toString());
		assertEquals("3", rn.getRight().toString());
	}

}
