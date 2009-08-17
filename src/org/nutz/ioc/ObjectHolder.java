package org.nutz.ioc;

class ObjectHolder<T> {

	ObjectHolder(T obj, ObjCallback<T> dep) {
		this.obj = obj;
		this.callback = dep;
	}

	private T obj;
	private ObjCallback<T> callback;

	T getObject() {
		return obj;
	}

	ObjCallback<T> getDeposer() {
		return callback;
	}

}
