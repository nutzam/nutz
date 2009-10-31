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
					Exception failInsert = null;
					try {
						dao.insert(ta);
					} catch (Exception e) {
						ta = dao.fetch(ta);
						failInsert = e;
					}
					if (null == ta) {
						if (null != failInsert)
							throw new RuntimeException(failInsert);
						throw new RuntimeException("You set null to param '@ta[2]'");
					}
					Object toValue = mta.getValue(ta, link.getTargetField());
					SqlMaker maker = ((NutDao) dao).getSqlMaker();
					Sql sql = maker.insert_manymany(link);
					sql.params().set(link.getFrom(), fromValue).set(link.getTo(), toValue);
					dao.execute(sql);
				}
			});
		}
	}
}