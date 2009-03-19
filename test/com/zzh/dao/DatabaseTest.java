package com.zzh.dao;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.dao.Dao;
import com.zzh.dao.One;
import com.zzh.dao.impl.FileSqlManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.lang.Files;

import junit.framework.TestCase;

/**
 * @author zozoh
 *
 */
public abstract class DatabaseTest extends TestCase {

	protected String dsFile;
	protected String sqlFile;
	private BasicDataSource dataSource;
	protected Dao dao;

	public DatabaseTest() {
		super();
	}

	public DatabaseTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		Properties pp = new Properties();
		pp.load(new FileInputStream(Files.findFile(dsFile)));
		dataSource = new BasicDataSource();
		dataSource.setUsername(pp.getProperty("username"));
		dataSource.setPassword(pp.getProperty("password"));
		dataSource.setUrl(pp.getProperty("url"));
		dataSource.setDriverClassName(pp.getProperty("driver"));
		// create dao
		dao = new NutDao(dataSource, new FileSqlManager(sqlFile));
		// create table;
		dao.execute(dao.sqls().createComboSql());
		// prepare data
		for (int i = 0; i < 20; i++) {
			dao.insert(One.make(i));
		}
	}

	@Override
	protected void tearDown() throws Exception {
		dataSource.close();
	}

}