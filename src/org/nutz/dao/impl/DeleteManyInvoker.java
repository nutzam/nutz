package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

class DeleteManyInvoker extends DeleteInvoker {
	
	DeleteManyInvoker(NutDao dao) {
		super(dao);
	}

	void invoke(Link link, Object many) {
		Object first = Lang.first(many);
		if (null != first) {
			final Entity<?> entity = dao.getEntity(first.getClass());
			Lang.each(many, new Each<Object>() {
				public void invoke(int i, Object obj, int length) throws ExitLoop, LoopException {
					dao._deleteSelf(entity, obj);
				}
			});
		}
	}
}
