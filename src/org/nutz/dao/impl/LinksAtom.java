package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.lang.Mirror;
import org.nutz.trans.Atom;

public abstract class LinksAtom implements Atom {

	protected NutDao dao;
	protected Entity<?> entity;
	protected Mirror<?> mirror;
	protected Links lns;

	protected Object ele;

	public LinksAtom setup(NutDao dao, Entity<?> entity, String regex, Mirror<?> mirror) {
		this.dao = dao;
		this.entity = entity;
		this.lns = new Links(null, entity, regex);
		this.mirror = mirror;
		return this;
	}

	public LinksAtom setEle(Object theObj) {
		this.ele = theObj;
		this.lns.setObj(theObj);
		return this;
	}

}
