package org.nutz.dao.impl;

import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.trans.Molecule;
import org.nutz.trans.Trans;

/**
 * 一个Dao辅助类.整合用户常用代码
 * 
 * @author wendal TODO 是放到nutz本身,还是放到nutzmore呢?
 */
public final class DaoHelper {

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
			@Override
			public void run() {
				List<T> list = dao.query(classOfT, cnd, pager);
				for (T t : list)
					dao.fetchLinks(t, regex);
				setObj(list);
			}
		};
		return exec(molecule);
	}

	public static <T> T exec(Molecule<T> molecule) {
		Trans.exec(molecule);
		return molecule.getObj();
	}
}
