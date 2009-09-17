package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlMaker;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

class InsertManyManyInvoker extends InsertInvoker {

	public InsertManyManyInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		super(dao, mainObj, mirror);
	}

	void invoke(final Link link, Object mm) {
		Object first = Lang.first(mm);
		if (null != first) {
			final Object fromValue = mirror.getValue(mainObj, link.getReferField());
			final Mirror<?> mta = Mirror.me(first.getClass());
			Lang.each(mm, new Each<Object>() {
				public void invoke(int i, Object ta, int length) {
					try {
						dao.insert(ta);
					} catch (Exception e) {
						ta = dao.fetch(ta);
					}
					Object toValue = mta.getValue(ta, link.getTargetField());
					//Sql<?> sql = dao.maker().makeInsertManyManySql(link, fromValue, toValue);
					SqlMaker maker = ((NutDao)dao).maker();
					Sql sql = maker.create(maker.ptn.INSERT_MANYMANY, link.getRelation());
					sql.vars().set("from", link.getFrom()).set("to", link.getTo());
					sql.params().set("from",fromValue).set("to", toValue);
					dao.execute(sql);
				}
			});
		}
	}
}