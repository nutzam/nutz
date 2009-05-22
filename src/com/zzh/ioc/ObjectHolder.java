package com.zzh.ioc;

class ObjectHolder<T> {

	ObjectHolder(T obj, Deposer<T> dep) {
		this.obj = obj;
		this.deposer = dep;
	}

	private T obj;
	private Deposer<T> deposer;

	T getObject() {
		return obj;
	}

	Deposer<T> getDeposer() {
		return deposer;
	}

}
