package com.zzh;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.zzh.dao.ComboSql;
import com.zzh.dao.Dao;
import com.zzh.dao.impl.FileSqlManager;
import com.zzh.dao.impl.NutDao;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.trans.TransactionTest;

public class Main {

	private static BasicDataSource dataSource;
	private static Properties pp = new Properties();

	/**
	 * It required 'nutz-test.properties' in classpath. the file should like:
	 * 
	 * <pre>
	 * driver=org.postgresql.Driver
	 * url=jdbc:postgresql://localhost:5432/zzhtest
	 * username=postgres
	 * password=123456
	 * </pre>
	 * 
	 * If mysql DB, is should like:
	 * 
	 * <pre>
	 * driver=com.mysql.jdbc.Driver
	 * url=jdbc:mysql://localhost:3306/zzhtest
	 * username=root
	 * password=123456
	 * engine=InnoDB
	 * </pre>
	 * 
	 * <b>Note:</b> If mysql, please make sure the table is InnoDB, else, the
	 * delay commit will not work.
	 * 
	 * @return DataSource object
	 */
	private static DataSource getDataSource() {
		try {
			pp.load(new FileInputStream(Files.findFile("nutz-test.properties")));
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		ENGIN = pp.getProperty("engin");
		dataSource = new BasicDataSource();
		dataSource.setUsername(getUserName());
		dataSource.setPassword(getPassword());
		dataSource.setUrl(getUrl());
		dataSource.setDriverClassName(getDriver());
		return dataSource;
	}

	public static String getDriver() {
		return pp.getProperty("driver");
	}

	public static String getUrl() {
		return pp.getProperty("url");
	}

	public static String getPassword() {
		return pp.getProperty("password");
	}

	public static String getUserName() {
		return pp.getProperty("username");
	}

	private static String ENGIN;

	public static String getEngin() {
		return ENGIN;
	}

	public static Dao getDao(String sqlPath) {
		if (null == sqlPath)
			return new NutDao(getDataSource());
		return new NutDao(getDataSource(), new FileSqlManager(sqlPath));
	}

	public static void closeDataSource() throws SQLException {
		if (null != dataSource) {
			dataSource.close();
			dataSource = null;
		}
	}

	public static void prepareTables(Dao dao, String[] keys) {
		ComboSql combo = dao.sqls().createComboSQL(keys);
		if (null != getEngin())
			combo.set(".engine", "ENGINE=" + getEngin());
		TransactionTest.dao.execute(combo);
	}

}
