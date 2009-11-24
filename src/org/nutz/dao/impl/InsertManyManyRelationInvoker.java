package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class InsertManyManyRelationInvoker extends InsertInvoker {

	public InsertManyManyRelationInvoker(Dao dao, Object mainObj, Mirror<?> mirror) {
		super(dao, mainObj, mirror);
	}

	@Override
	void invoke(final Link link, Object mm) {
		Object first = Lang.first(mm);
		if (null != first) {
			final Object fromValue = mirror.getValue(mainObj, link.getReferField());
			final Mirror<?> mta = Mirror.me(first.getClass());
			Lang.each(mm, new Each<Object>() {
				public void invoke(int i, Object ta, int length) {
					insertManyManyRelation(link, fromValue, mta, ta);
				}
			});
		}
	}

}
