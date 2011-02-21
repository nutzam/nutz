package org.nutz.dao.texp;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Expression;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;
import org.nutz.lang.Lang;

public class CndTest extends DaoCase {

	private Entity<?> en;

	protected void before() {
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/texp/worker.dod"));
		en = dao.getEntity(Worker.class);
	}

	protected void after() {}

	@Test
	public void test_gt_like() {
		Condition c = Cnd.where("id", ">", 45).and("name", "LIKE", "%ry%");
		String exp = "wid>45 AND wname LIKE '%ry%'";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test_bracket() {
		Condition c = Cnd.where(Cnd.exps("id", ">", 45)).and("name", "LIKE", "%ry%");
		String exp = "(wid>45) AND wname LIKE '%ry%'";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test_order() {
		Condition c = Cnd.orderBy().asc("id").desc("name").asc("age").desc("workingDay");
		String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test_like_in() {
		int[] ages = {4, 7, 9};
		Expression e = Cnd.exps("age", ">", 35).and("id", "<", 47);
		Expression e2 = Cnd.exps("name", "\tLIKE ", "%t%").and("age", "IN  \n\r", ages).or(e);
		Condition c = Cnd.where("id", "=", 37).and(e).or(e2).asc("age").desc("id");
		String exp = "wid=37 AND (age>35 AND wid<47) OR (wname LIKE '%t%' AND age IN (4,7,9) OR (age>35 AND wid<47)) ORDER BY age ASC, wid DESC";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test_equel() {
		Condition c = Cnd.where("ff", "=", true);
		String exp = "ff=true";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test_in_by_int_array() {
		int[] ids = {3, 5, 7};
		Condition c = Cnd.where("id", "iN", ids);
		String exp = "id IN (3,5,7)";
		assertEquals(exp, c.toSql(null));
	}

	@Test
	public void test_in_by_int_list() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(3);
		list.add(5);
		list.add(7);
		Condition c = Cnd.where("id", "iN", list);
		String exp = "id IN (3,5,7)";
		assertEquals(exp, c.toSql(null));
	}

	@Test
	public void test_in_by_str_array() {
		Condition c = Cnd.where("nm", "iN", Lang.array("'A'", "B"));
		String exp = "nm IN ('''A''','B')";
		assertEquals(exp, c.toSql(null));
	}

	@Test
	public void test_in_by_str_list() {
		List<String> list = new ArrayList<String>();
		list.add("'A'");
		list.add("B");
		Condition c = Cnd.where("nm", "iN", list);
		String exp = "nm IN ('''A''','B')";
		assertEquals(exp, c.toSql(null));
	}

	@Test
	public void test_is_null() {
		Condition c = Cnd.where("nm", " is ", null);
		String exp = "nm IS NULL";
		assertEquals(exp, c.toSql(null));
	}
	
	@Test
	public void test_is_not_null(){
		Condition c = Cnd.where("nm", " is nOT ", null);
		String exp = "nm IS NOT NULL";
		assertEquals(exp, c.toSql(null));
	}

	@Test
	public void test_not_in(){
		Condition c = Cnd.where("nm", " Not iN ", new int[]{1,2,3});
		String exp = "nm NOT IN (1,2,3)";
		assertEquals(exp, c.toSql(null));
	}
}
