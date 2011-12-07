package org.nutz.dao.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;

/**
 * Dao 的帮助函数，基本上，你不会用到这个类
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Daos {

	public static void safeClose(Statement stat, ResultSet rs) {
		safeClose(rs);
		safeClose(stat);
	}

	public static void safeClose(Statement stat) {
		if (null != stat)
			try {
				stat.close();
			}
			catch (Throwable e) {}
	}

	public static void safeClose(ResultSet rs) {
		if (null != rs)
			try {
				rs.close();
			}
			catch (Throwable e) {}
	}

	public static int getColumnIndex(ResultSetMetaData meta, String colName) throws SQLException {
		if (meta == null)
			return 0;
		int columnCount = meta.getColumnCount();
		for (int i = 1; i <= columnCount; i++)
			if (meta.getColumnName(i).equalsIgnoreCase(colName))
				return i;
		// TODO 尝试一下meta.getColumnLabel?
		throw Lang.makeThrow(SQLException.class, "Can not find @Column(%s)", colName);
	}

	public static boolean isIntLikeColumn(ResultSetMetaData meta, int index) throws SQLException {
		switch (meta.getColumnType(index)) {
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.NUMERIC:
			return true;
		}
		return false;
	}

	public static Pager updatePagerCount(Pager pager, Dao dao, Class<?> entityType, Condition cnd) {
		if (null != pager) {
			pager.setRecordCount(dao.count(entityType, cnd));
		}
		return pager;
	}

	public static Pager updatePagerCount(Pager pager, Dao dao, String tableName, Condition cnd) {
		if (null != pager) {
			pager.setRecordCount(dao.count(tableName, cnd));
		}
		return pager;
	}

	public static <T> List<T> queryList(Dao dao, Class<T> klass, String sql_str) {
		Sql sql = Sqls.create(sql_str)
						.setCallback(Sqls.callback.entities())
						.setEntity(dao.getEntity(klass));
		dao.execute(sql);
		return sql.getList(klass);
	}

	public static Object query(Dao dao, String sql_str, SqlCallback callback) {
		Sql sql = Sqls.create(sql_str).setCallback(callback);
		dao.execute(sql);
		return sql.getResult();
	}

	public static <T> List<T> queryWithLinks(	final Dao dao,
												final Class<T> classOfT,
												final Condition cnd,
												final Pager pager,
												final String regex) {
		Molecule<List<T>> molecule = new Molecule<List<T>>() {
			public void run() {
				List<T> list = dao.query(classOfT, cnd, pager);
				for (T t : list)
					dao.fetchLinks(t, regex);
				setObj(list);
			}
		};
		return Trans.exec(molecule);
	}

}
