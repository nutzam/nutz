package com.zzh.ioc.impl;

import com.zzh.ioc.Deposer;

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
