package com.zzh.trans;

public abstract class ServiceAtom<T> implements Atom {

	protected T service;

	public ServiceAtom(T service) {
		this.service = service;
	}

}
