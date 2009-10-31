package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;

class DeleteManyManyInvoker extends DeleteInvoker {

	DeleteManyManyInvoker(NutDao dao) {
		super(dao);
	}

	void invoke(final Link link, Object mm) {
		Object first = Lang.first(mm);
		if (null != first) {
			final Entity<?> entity = dao.getEntity(first.getClass());
			Lang.each(mm, new Each<Object>() {
				public void invoke(int i, Object ta, int length) throws ExitLoop, LoopException {
					if (null != ta) {
						dao._deleteSelf(entity, ta);
						Object value = Mirror.me(ta.getClass()).getValue(ta, link.getTargetField());
						Sql sql = dao.getSqlMaker().clear_links(link.getRelation(), link.getTo(),
								link.getTo());
						sql.params().set(link.getTo(), value);
						dao.execute(sql);
					}
				}
			});
		}
	}

}
