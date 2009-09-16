package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import org.nutz.dao.ComboSql;
import org.nutz.dao.ExecutableSql;
import org.nutz.dao.FetchSql;
import org.nutz.dao.QuerySql;
import org.nutz.dao.Sql;
import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.lang.Files;

public class SQLFileParsing {

	private static final String PATH = "org/nutz/dao/test/sqls/sqls.sqls";

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
		Sql<?> sql = sqls.create(".abc.create");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Insert_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.create(".abc.insert");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Update_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.create(".abc.update");
		assertTrue(sql instanceof ExecutableSql);
	}

	@Test
	public void check_Fetch_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.create("abc.fetch");
		assertTrue(sql instanceof FetchSql<?>);
	}

	@Test
	public void check_Query_SQL() {
		sqls = new FileSqlManager(PATH);
		Sql<?> sql = sqls.create("abc.query");
		assertTrue(sql instanceof QuerySql<?>);
	}

	@Test
	public void check_PersonTestSQLs() {
		sqls = new FileSqlManager("org/nutz/dao/test/sqls/sqls.sqls");
		String[] keys = { ".abc.drop", ".abc.create", ".abc.insert", ".abc.update", "abc.fetch",
				"abc.query", ".student.drop", ".student.create", ".student2.drop",
				".student2.create" };
		for (int i = 0; i < keys.length; i++) {
			assertEquals(keys[i], sqls.keys()[i]);
		}
	}

	@Test
	public void check_parse_comboSqls() {
		sqls = new FileSqlManager("org/nutz/dao/test/sqls/sqls.sqls");
		ComboSql sql = sqls.createComboSql();
		assertEquals(10, sql.count());
	}

	@Test
	public void test_sqls_save() throws IOException{
		sqls = new FileSqlManager("org/nutz/dao/test/sqls/sqls.sqls");
		int count = sqls.count();
		File f = Files.findFile("org/nutz/dao/test/sqls/save.sqls");
		((FileSqlManager)sqls).saveAs(f.getAbsolutePath());
		sqls = new FileSqlManager("org/nutz/dao/test/sqls/save.sqls");
		assertEquals(count,sqls.count());
	}
}
