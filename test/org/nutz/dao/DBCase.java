package org.nutz.dao;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;

import org.nutz.dao.Dao;
import org.nutz.dao.DBObject;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.lang.Files;

public abstract class DBCase{

	protected String dsFile;
	protected String sqlFile;
	private BasicDataSource dataSource;
	protected Dao dao;

	@Before
	public void setUp() throws Exception {
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
		dao.execute(dao.sqls().createCombo());
		// prepare data
		for (int i = 0; i < 20; i++) {
			dao.insert(DBObject.make(i));
		}
	}

	@After
	public void tearDown() throws Exception {
		dataSource.close();
	}

}