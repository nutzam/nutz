package org.nutz.dom;

import java.util.HashMap;
import java.util.Map;

public abstract class DomBuilder {

	private Map<Class<?>, Map<Class<?>, Listener<?, ?>>> map;

	protected DomBuilder() {
		map = new HashMap<Class<?>, Map<Class<?>, Listener<?, ?>>>();
	}

	public synchronized DomBuilder register(Listener<?, ?> lsn) {
		Map<Class<?>, Listener<?, ?>> sub = map.get(lsn.getHandleType());
		if (null == sub) {
			sub = new HashMap<Class<?>, Listener<?, ?>>();
			map.put(lsn.getHandleType(), sub);
		}
		sub.put(lsn.getReferType(), lsn);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T, R> Listener<T, R> getListener(Class<T> handleType, Class<R> referType) {
		Map<Class<?>, Listener<?, ?>> sub = map.get(handleType);
		if (null == sub)
			return null;
		return (Listener<T, R>) sub.get(referType);
	}

}
