package org.nutz.dao.texp;

import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.Expression;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityHolder;
import org.nutz.dao.entity.impl.DefaultEntityMaker;

import junit.framework.TestCase;

public class CndTest extends TestCase {

	private static final EntityHolder holder = new EntityHolder(DefaultEntityMaker.class);

	private Entity en;

	@Override
	protected void setUp() throws Exception {
		en = holder.getEntity(Worker.class, new DatabaseMeta());
	}

	public void test00() {
		Condition c = Cnd.where("id", ">", 45).and("name", "LIKE", "%ry%");
		String exp = "wid>45 AND wname LIKE '%ry%'";
		assertEquals(exp, c.toString(en));
	}

	public void test01() {
		Condition c = Cnd.where(Cnd.exps("id", ">", 45)).and("name", "LIKE", "%ry%");
		String exp = "(wid>45) AND wname LIKE '%ry%'";
		assertEquals(exp, c.toString(en));
	}

	public void test02() {
		Condition c = Cnd.orderBy().asc("id").desc("name").asc("age").desc("workingDay");
		String exp = "ORDER BY wid ASC, wname DESC, age ASC, days DESC";
		assertEquals(exp, c.toString(en));
	}

	public void test03() {
		Expression e = Cnd.exps("age", ">", 35).and("id", "<", 47);
		Expression e2 = Cnd.exps("name", "\tLIKE ", "%t%").and("age", "IN  \n\r", "(4,7,9)").or(e);
		Condition c = Cnd.where("id", "=", 37).and(e).or(e2).asc("age").desc("id");
		String exp = "wid=37 AND (age>35 AND wid<47) OR (wname LIKE '%t%' AND age IN '(4,7,9)' OR (age>35 AND wid<47)) ORDER BY age ASC, wid DESC";
		assertEquals(exp, c.toString(en));
	}

	public void test04() {
		Condition c = Cnd.where("ff", "=", true);
		String exp = "ff=true";
		assertEquals(exp, c.toString(en));
	}

}
