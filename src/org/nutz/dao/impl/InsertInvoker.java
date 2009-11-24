package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlMaker;
import org.nutz.lang.Mirror;

abstract class InsertInvoker extends LinkInvoker {

	Dao dao;
	Object mainObj;
	Mirror<?> mirror;

	public InsertInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		this.dao = dao;
		this.mainObj = mainObj;
		this.mirror = mirror;
	}

	protected void insertManyManyRelation(	final Link link,
											final Object fromValue,
											final Mirror<?> mta,
											Object ta) {
		Object toValue = mta.getValue(ta, link.getTargetField());
		SqlMaker maker = ((NutDao) dao).getSqlMaker();
		Sql sql = maker.insert_manymany(link);
		sql.params().set(link.getFrom(), fromValue).set(link.getTo(), toValue);
		dao.execute(sql);
	}

}
