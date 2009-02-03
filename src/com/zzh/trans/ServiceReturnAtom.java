package com.zzh.trans;

public abstract class ServiceReturnAtom<T, R> extends ServiceAtom<T> {

	public ServiceReturnAtom(T service) {
		super(service);
	}

	private R object;

	public R getObject() {
		return object;
	}

	protected void setObject(R obj) {
		this.object = obj;
	}

}
