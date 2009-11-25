package org.nutz.dao.texp;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Expression;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;

public class CndTest extends DaoCase {

	private Entity<?> en;

	protected void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/texp/worker.dod"));
		en = dao.getEntity(Worker.class);
	}

	protected void after() {}

	@Test
	public void test00() {
		Condition c = Cnd.where("id", ">", 45).and("name", "LIKE", "%ry%");
		String exp = "wid>45 AND wname LIKE '%ry%'";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test01() {
		Condition c = Cnd.where(Cnd.exps("id", ">", 45)).and("name", "LIKE", "%ry%");
		String exp = "(wid>45) AND wname LIKE '%ry%'";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test02() {
		Condition c = Cnd.orderBy().asc("id").desc("name").asc("age").desc("workingDay");
		String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test03() {
		Expression e = Cnd.exps("age", ">", 35).and("id", "<", 47);
		Expression e2 = Cnd.exps("name", "\tLIKE ", "%t%").and("age", "IN  \n\r", "(4,7,9)").or(e);
		Condition c = Cnd.where("id", "=", 37).and(e).or(e2).asc("age").desc("id");
		String exp = "wid=37 AND (age>35 AND wid<47) OR (wname LIKE '%t%' AND age IN '(4,7,9)' OR (age>35 AND wid<47)) ORDER BY age ASC, wid DESC";
		assertEquals(exp, c.toSql(en));
	}

	@Test
	public void test04() {
		Condition c = Cnd.where("ff", "=", true);
		String exp = "ff=true";
		assertEquals(exp, c.toSql(en));
	}

}
