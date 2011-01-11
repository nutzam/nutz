package org.nutz.dao.sql;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.nutz.json.Json;

public class SqlLiteralTest {

	private static SqlLiteral L(String s) {
		return new SqlLiteral().valueOf(s);
	}

	@Test
	public void test_chinese_var_name() {
		SqlLiteral sql = L("INSERT INTO t_chin(名称,描述) VALUES($名,$述)");
		assertEquals(2, sql.getVarIndexes().size());
		Iterator<String> nms = sql.getVarIndexes().names().iterator();
		assertEquals("名", nms.next());
		assertEquals("述", nms.next());

		String expect = "INSERT INTO t_chin(名称,描述) VALUES(,)";
		assertEquals(expect, sql.toPreparedStatementString());
		sql.getVars().set("名", "老张");
		sql.getVars().set("述", "很棒");
		expect = "INSERT INTO t_chin(名称,描述) VALUES(老张,很棒)";
		assertEquals(expect, sql.toString());
	}

	@Test
	public void test_chinese_param_name() {
		SqlLiteral sql = L("INSERT INTO t_chin(名称,描述) VALUES(@名,@述)");
		assertEquals(2, sql.getParamIndexes().size());
		Iterator<String> nms = sql.getParamIndexes().names().iterator();
		assertEquals("名", nms.next());
		assertEquals("述", nms.next());

		String expect = "INSERT INTO t_chin(名称,描述) VALUES(?,?)";
		assertEquals(expect, sql.toPreparedStatementString());
		sql.getParams().set("名", "老张");
		sql.getParams().set("述", "很棒");
		expect = "INSERT INTO t_chin(名称,描述) VALUES('老张','很棒')";
		assertEquals(expect, sql.toString());
	}

	@Test
	public void test_name_with_underline() {
		SqlLiteral sql = L("@a_1:$a_1");
		sql.getParams().set("a_1", "A");
		sql.getVars().set("a_1", "B");
		assertEquals("'A':B", sql.toString());
		assertEquals("?:B", sql.toPreparedStatementString());
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

	@Test
	public void test_dot_with_var() {
		SqlLiteral sql = L("$x.y");
		sql.getVars().set("x", "T");
		assertEquals("T.y", sql.toString());
	}

	@Test
	public void test_dot_with_param() {
		SqlLiteral sql = L("@x.y");
		sql.getParams().set("x", "T");
		assertEquals("'T'.y", sql.toString());
	}

	@Test
	public void test_param_names() {
		SqlLiteral sql = L("UPDATE dao_platoon SET name=@name1,base=@baseName2,leader=@leaderName3 WHERE id=@id4");
		String[] paramNames = sql.getParamNames();
		String result[] = {"leaderName3", "id4", "baseName2", "name1"};
		Arrays.sort(paramNames);
		Arrays.sort(result);
		assertArrayEquals(paramNames, result);
	}

	@Test
	public void test_var_names() {
		SqlLiteral sql = L("InSeRT INTO $T ($id,$name) VALUES(@id1,@name2)");
		String[] varNames = sql.getVarNames();
		String result[] = {"T", "name", "id"};
		//System.out.println(Json.toJson(varNames));

		Arrays.sort(varNames);
		Arrays.sort(result);
		assertArrayEquals(varNames, result);
	}
}
