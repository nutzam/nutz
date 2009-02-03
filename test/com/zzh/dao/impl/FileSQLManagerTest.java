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

	@Override
	protected void setUp() throws Exception {
		sqls = new FileSqlManager("com/zzh/dao/impl/test.sqls");
	}

	public void testCountSQL() {
		assertEquals(8, sqls.count());
	}

	public void testCreateSQL() {
		Sql<?> sql = sqls.createSql(".abc.create");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testInsertSQL() {
		Sql<?> sql = sqls.createSql(".abc.insert");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testUpdateSQL() {
		Sql<?> sql = sqls.createSql(".abc.update");
		assertTrue(sql instanceof ExecutableSql);
	}

	public void testFetchSQL() {
		Sql<?> sql = sqls.createSql("fetch.abc");
		assertTrue(sql instanceof FetchSql);
	}

	public void testQuerySQL() {
		Sql<?> sql = sqls.createSql("query.abc");
		assertTrue(sql instanceof QuerySql);
	}

}
