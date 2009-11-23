package org.nutz.dao.sql;

import static org.junit.Assert.*;

import org.junit.Test;

public class SqlLiteralTest {

	private static SqlLiteral L(String s) {
		return new SqlLiteral().valueOf(s);
	}

	@Test
	public void test_simple() {
		SqlLiteral sql = L("A$a B@a C@b D$condition");
		sql.getVars().set("a", "T");
		sql.getParams().set("a", 23);
		sql.getParams().set("b", false);
		assertEquals("AT B? C? D", sql.toPreparedStatementString());
		assertEquals("AT B23 Cfalse D", sql.toString());
	}

	@Test
	public void test_holder_var_escaping() {
		SqlLiteral sql = L("@@@@$$T$%$a@a;");
		sql.getVars().set("a", "V");
		sql.getParams().set("a", "H");
		assertEquals("@@$T$%V?;", sql.toPreparedStatementString());
		assertEquals("@@$T$%V'H';", sql.toString());
	}

	@Test
	public void test_sql_types() {
		assertTrue(L("InSeRT INTO $T ($id,$name) VALUES(@id,@name)").isINSERT());
		assertTrue(L("UPDaTE $T SET $id=@id").isUPDATE());
		assertTrue(L("sELECT * FROM $T").isSELECT());
		assertTrue(L("DeLETE FROM $T").isDELETE());
		assertTrue(L("Drop table $T").isDROP());
		assertTrue(L("crEATE table abc(id INT)").isCREATE());
	}

	@Test
	public void test_var_set_index() {
		SqlLiteral sql = L("$A,$B,@C,@D,@C");
		int[] is = sql.getParamIndexes("C");
		assertEquals(1, is[0]);
		assertEquals(3, is[1]);
		is = sql.getParamIndexes("D");
		assertEquals(2, is[0]);
	}

	@Test
	public void test_toPreparedStatementString() {
		SqlLiteral sql = L("=@a=@b");
		String exp = "=?=?";
		String actural = sql.toPreparedStatementString();
		assertEquals(exp, actural);

	}

	@Test
	public void test_toPreparedStatementString_regularly() {
		SqlLiteral sql = L("UPDATE dao_platoon SET name=@name,base=@baseName,leader=@leaderName WHERE id=@id");
		String exp = "UPDATE dao_platoon SET name=?,base=?,leader=? WHERE id=?";
		String actural = sql.toPreparedStatementString();
		assertEquals(exp, actural);

	}
}
