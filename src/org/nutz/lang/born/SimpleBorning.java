package org.nutz.lang.born;

import java.lang.reflect.Type;

import org.nutz.lang.Mirror;

public class SimpleBorning<T> implements Borning<T> {

	private BorningInvoker<T> invoker;
	private Object[] args;

	public SimpleBorning(BorningInvoker<T> invoker, Object[] args) {
		this.invoker = invoker;
		this.args = args;
	}

	public T born() {
		return born(args);
	}

	public T born(Object[] args) {
		try {
			return invoker.born(args);
		} catch (Throwable e) {
			Class<?> type = null;
			Type[] tps = Mirror.getTypeParams(getClass());
			if (null != tps && tps.length > 0)
				type = (Class<?>) tps[0];
			throw new BorningException(e, type, args);
		}
	}

}
