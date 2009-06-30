package org.nutz.dao.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;

final class Links {

	Links(Object obj, Entity<?> en, String regex) {
		this.obj = obj;
		this.entity = en;
		ones = new LinkedList<Link>();
		manys = new LinkedList<Link>();
		mms = new LinkedList<Link>();
		all = en.getLinks(regex);
		if (null != all)
			for (Iterator<Link> it = all.iterator(); it.hasNext();) {
				Link ln = it.next();
				if (ln.isOne())
					ones.add(ln);
				else if (ln.isMany())
					manys.add(ln);
				else
					mms.add(ln);
			}

	}

	private List<Link> ones;
	private List<Link> manys;
	private List<Link> mms;
	private List<Link> all;
	private Object obj;
	private Entity<?> entity;

	private void invoke(LinkInvoker walker, List<Link> list) {
		if (null != list)
			for (Iterator<Link> it = list.iterator(); it.hasNext();) {
				Link link = it.next();
				Object value = entity.getMirror().getValue(obj, link.getOwnField());
				if (null != value)
					walker.invoke(link, value);
			}
	}

	void invokeOnes(LinkInvoker invoker) {
		invoke(invoker, ones);
	}

	void invokeManys(LinkInvoker invoker) {
		invoke(invoker, manys);
	}

	void invokeManyManys(LinkInvoker invoker) {
		invoke(invoker, mms);
	}

	void invokeAll(LinkInvoker invoker) {
		invoke(invoker, all);
	}

	private void walk(LinkWalker walker, List<Link> list) {
		if (null != list)
			for (Iterator<Link> it = list.iterator(); it.hasNext();) {
				walker.walk(it.next());
			}
	}

	void walkOnes(LinkWalker walker) {
		walk(walker, ones);
	}

	void walkManys(LinkWalker walker) {
		walk(walker, manys);
	}

	void walkManyManys(LinkWalker walker) {
		walk(walker, mms);
	}

	void walkAll(LinkWalker walker) {
		walk(walker, all);
	}

}
