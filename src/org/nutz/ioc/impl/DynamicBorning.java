package org.nutz.ioc.impl;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.MirrorBorning;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.BorningInvoker;
import org.nutz.lang.born.SimpleBorning;

public class DynamicBorning<T> implements Borning<T> {

	private BorningInvoker<T> invoker;

	private ValueDelegate[] vds;

	DynamicBorning(Mirror<T> mirror, ValueDelegate[] vds) {
		this.vds = vds;
		MirrorBorning<T> borning = mirror.getBorning(makeArray());
		invoker = borning.getInvoker();
	}

	private Object[] makeArray() {
		Object[] objs = new Object[vds.length];
		int i = 0;
		for (ValueDelegate vd : vds)
			objs[i++] = vd.get();
		return objs;
	}

	Borning<T> toStatic() {
		return new SimpleBorning<T>(invoker, makeArray());
	}

	public T born() {
		return born(makeArray());
	}

	public T born(Object[] args) {
		try {
			return invoker.born(args);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
