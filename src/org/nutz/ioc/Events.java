package org.nutz.ioc;

public class Events<T> {

	private ObjCallback<T> whenCreate;

	private ObjCallback<T> whenFetch;

	private ObjCallback<T> whenDepose;

	public ObjCallback<T> getWhenCreate() {
		return whenCreate;
	}

	public void setWhenCreate(ObjCallback<T> whenCreate) {
		this.whenCreate = whenCreate;
	}

	public ObjCallback<T> getWhenFetch() {
		return whenFetch;
	}

	public void setWhenFetch(ObjCallback<T> whenFetch) {
		this.whenFetch = whenFetch;
	}

	public ObjCallback<T> getWhenDepose() {
		return whenDepose;
	}

	public void setWhenDepose(ObjCallback<T> whenDepose) {
		this.whenDepose = whenDepose;
	}

}
