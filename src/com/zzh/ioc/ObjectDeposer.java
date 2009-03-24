package com.zzh.ioc;

import com.zzh.lang.Mirror;

public abstract class ObjectDeposer<T> implements Deposer {

	@SuppressWarnings("unchecked")
	public ObjectDeposer(String name) {
		this.name = name;
		this.type = (Class<T>) Mirror.getTypeParams(getClass())[0];
	}

	private String name;
	private Class<T> type;

	@Override
	public void depose(Ioc ioc) {
		try {
			T obj = ioc.getObject(type, name);
			depose(obj);
		} catch (ObjectNotFoundException e) {}
	}

	protected abstract void depose(T obj);

}
