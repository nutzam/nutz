package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;
import org.nutz.trans.Trans;

class LinksRunner<T> {

	private NutDao dao;
	private T obj;
	private LinksAtom atom;
	private String regex;
	private boolean transaction;

	LinksRunner(NutDao dao, T obj, String regex, LinksAtom atom) {
		this.dao = dao;
		this.obj = obj;
		this.atom = atom;
		this.regex = regex;
		this.transaction = true;
	}

	public LinksRunner<T> setTransaction(boolean transaction) {
		this.transaction = transaction;
		return this;
	}

	T run() {
		if (null == obj)
			return null;

		int len = Lang.length(obj);
		if (len == 0)
			return obj;

		Object ele = Lang.first(obj);
		Class<?> eleType = ele.getClass();
		final Entity<?> entity = dao.getEntity(eleType);
		atom.setup(dao, entity, regex, (Mirror<?>) Mirror.me(eleType));

		Lang.each(obj, new Each<Object>() {
			public void invoke(int i, final Object ele, int length) throws ExitLoop, LoopException {
				if (transaction)
					Trans.exec(atom.setEle(ele));
				else
					atom.setEle(ele).run();
			}
		});
		return obj;
	}

}
