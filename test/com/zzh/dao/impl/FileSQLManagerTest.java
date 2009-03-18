package com.zzh.dao.impl;

import com.zzh.dao.ExecutableSql;
import com.zzh.dao.FetchSql;
import com.zzh.dao.QuerySql;
import com.zzh.dao.Sql;
import com.zzh.dao.SqlManager;
import com.zzh.dao.impl.FileSqlManager;

import junit.framework.TestCase;

public class FileSQLManagerTest extends TestCase {

	private SqlManager sqls;

	public void testCountSQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		assertEquals(8, sqls.count());
		String[] keys = { ".abc.drop", ".abc.create", ".abc.insert", ".abc.update", "abc.fetch",
				"abc.query", ".student.drop", ".student.create" };
		for (int i = 0; i < keys.length; i++) {
			assertEquals(keys[i], sqls.keys()[i]);
		}
	}

	public void testCreateSQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		Sql<?> sql = sqls.createSql(".abc.create");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testInsertSQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		Sql<?> sql = sqls.createSql(".abc.insert");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testUpdateSQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		Sql<?> sql = sqls.createSql(".abc.update");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testFetchSQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		Sql<?> sql = sqls.createSql("abc.fetch");
		assertTrue(sql instanceof FetchSql);
	}

	public void testQuerySQL() {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
		Sql<?> sql = sqls.createSql("abc.query");
		assertTrue(sql instanceof QuerySql);
	}

	public void testPersonTestSQLs() {
		sqls = new FileSqlManager("com/zzh/dao/impl/personTest.sqls");
		String[] keys = { ".drop", ".create", ".profile.drop", ".profile.create" };
		for (int i = 0; i < keys.length; i++) {
			assertEquals(keys[i], sqls.keys()[i]);
		}
	}

}
