package org.nutz.ioc.impl;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.MirrorBorning;
import org.nutz.lang.born.Borning;
import org.nutz.lang.born.BorningInvoker;

public class DynamicBorning<T> implements Borning<T> {

	private BorningInvoker<T> invoker;

	private ValueDelegate[] vds;

	DynamicBorning(Mirror<T> mirror, ValueDelegate[] vds) {
		this.vds = vds;
		MirrorBorning<T> borning = mirror.getBorning(makeArray());
		invoker = borning.getBorningInvoker();
	}

	private Object[] makeArray() {
		Object[] objs = new Object[vds.length];
		int i = 0;
		for (ValueDelegate vd : vds)
			objs[i++] = vd.get();
		return objs;
	}

	public Borning<T> getBorning() {
		return invoker;
	}

	public T born() {
		try {
			return invoker.born(makeArray());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
