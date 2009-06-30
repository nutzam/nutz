package org.nutz.ioc;

class ObjectHolder<T> {

	ObjectHolder(T obj, Callback<T> dep) {
		this.obj = obj;
		this.callback = dep;
	}

	private T obj;
	private Callback<T> callback;

	T getObject() {
		return obj;
	}

	Callback<T> getDeposer() {
		return callback;
	}

}
