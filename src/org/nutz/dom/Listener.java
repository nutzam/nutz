package org.nutz.dom;

import org.nutz.lang.Mirror;

public abstract class Listener<T, R> {

	private Class<T> handleType;
	private Class<R> referType;

	@SuppressWarnings("unchecked")
	protected Listener() {
		handleType = (Class<T>) Mirror.getTypeParams(getClass())[0];
		referType = (Class<R>) Mirror.getTypeParams(getClass())[1];
	}

	public Class<T> getHandleType() {
		return handleType;
	}

	public Class<R> getReferType() {
		return referType;
	}

	public abstract void handle(T node, R refer);

}
