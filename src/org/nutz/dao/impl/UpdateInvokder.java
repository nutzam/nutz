package org.nutz.dao.impl;

import org.nutz.dao.Dao;
import org.nutz.dao.entity.Link;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

class UpdateInvokder extends LinkInvoker {

	private Dao dao;

	UpdateInvokder(Dao dao) {
		this.dao = dao;
	}

	void invoke(Link link, Object objSet) {
		Lang.each(objSet, new Each<Object>() {
			public void invoke(int i, Object ele, int length) throws ExitLoop, LoopException {
				dao.update(ele);
			}
		});
	}
}
