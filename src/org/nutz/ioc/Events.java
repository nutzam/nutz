package org.nutz.ioc;

public class Events<T> {

	private Callback<T> whenCreate;

	private Callback<T> whenFetch;

	private Callback<T> whenDepose;

	public Callback<T> getWhenCreate() {
		return whenCreate;
	}

	public void setWhenCreate(Callback<T> whenCreate) {
		this.whenCreate = whenCreate;
	}

	public Callback<T> getWhenFetch() {
		return whenFetch;
	}

	public void setWhenFetch(Callback<T> whenFetch) {
		this.whenFetch = whenFetch;
	}

	public Callback<T> getWhenDepose() {
		return whenDepose;
	}

	public void setWhenDepose(Callback<T> whenDepose) {
		this.whenDepose = whenDepose;
	}

}
