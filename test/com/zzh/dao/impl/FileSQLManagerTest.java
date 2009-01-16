package com.zzh.dao.impl;

import com.zzh.dao.ExecutableSQL;
import com.zzh.dao.FetchSQL;
import com.zzh.dao.QuerySQL;
import com.zzh.dao.SQL;
import com.zzh.dao.SQLManager;
import com.zzh.dao.impl.FileSQLManager;

import junit.framework.TestCase;

public class FileSQLManagerTest extends TestCase {

	private SQLManager sqls;

	@Override
	protected void setUp() throws Exception {
		sqls = new FileSQLManager("com/zzh/dao/impl/test.sqls");
	}

	public void testCountSQL() {
		assertEquals(8, sqls.count());
	}

	public void testCreateSQL() {
		SQL<?> sql = sqls.createSQL(".abc.create");
		assertTrue(sql instanceof ExecutableSQL);
	}

	public void testInsertSQL() {
		SQL<?> sql = sqls.createSQL(".abc.insert");
		assertTrue(sql instanceof ExecutableSQL);
	}

	public void testUpdateSQL() {
		SQL<?> sql = sqls.createSQL(".abc.update");
		assertTrue(sql instanceof ExecutableSQL);
	}

	public void testFetchSQL() {
		SQL<?> sql = sqls.createSQL("fetch.abc");
		assertTrue(sql instanceof FetchSQL);
	}

	public void testQuerySQL() {
		SQL<?> sql = sqls.createSQL("query.abc");
		assertTrue(sql instanceof QuerySQL);
	}

}
