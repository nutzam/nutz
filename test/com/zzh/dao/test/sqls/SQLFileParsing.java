package com.zzh.dao.test.sqls;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.SqlManager;
import com.zzh.dao.impl.FileSqlManager;

public class SQLFileParsing {

	private static final String PATH = "com/zzh/dao/test/sqls/sqls.sqls";

	private SqlManager sqls;

	@Test
	public void check_Count_SQL() {
		sqls = new FileSqlManager(PATH);
		assertEquals(10, sqls.count());
		String[] keys = { ".abc.drop", ".abc.create", ".abc.insert", ".abc.update", "abc.fetch",
				"abc.query", ".student.drop", ".student.create" };
		for (int i = 0; i < keys.length; i++) {
			assertEquals(keys[i], sqls.keys()[i]);
		}
	}

	@Test
	public void check_Create_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.createSql(".abc.create");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Insert_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.createSql(".abc.insert");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Update_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.createSql(".abc.update");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Fetch_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.createSql("abc.fetch");
		assertTrue(sql instanceof FetchSql);
	}

	@Test
	public void check_Query_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.createSql("abc.query");
		assertTrue(sql instanceof QuerySql);
	}

	@Test
	public void check_PersonTestSQLs() {
		sqls = new FileSqlManager("com/zzh/dao/test/sqls/sqls.sqls");
		String[] keys = { ".abc.drop", ".abc.create", ".abc.insert", ".abc.update", "abc.fetch",
				"abc.query", ".student.drop", ".student.create", ".student2.drop",
				".student2.create" };
		for (int i = 0; i < keys.length; i++) {
			assertEquals(keys[i], sqls.keys()[i]);
		}
	}

}
