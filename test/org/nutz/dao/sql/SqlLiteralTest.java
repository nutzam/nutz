package org.nutz.dao.sql;

import static org.junit.Assert.*;

import org.junit.Test;

public class SqlLiteralTest {

	@Test
	public void test_simple() {
		SqlLiteral sql = new SqlLiteral();
		sql.valueOf("A$a B@a C@b D$condition");
		sql.vars().set("a", "T");
		sql.holders().set("a", 23);
		sql.holders().set("b", false);
		assertEquals("AT B? C? D", sql.toPrepareStatementString());
		assertEquals("AT B23 Cfalse D", sql.toString());
	}

	@Test
	public void test_holder_var_escaping() {
		SqlLiteral sql = new SqlLiteral();
		sql.valueOf("@@@@$$T$%$a@a;");
		sql.vars().set("a", "V");
		sql.holders().set("a", "H");
		assertEquals("@@$T$%V?;", sql.toPrepareStatementString());
		assertEquals("@@$T$%VH;", sql.toString());
	}
}
