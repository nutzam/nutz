package com.zzh.mvc.c;

import com.zzh.mvc.Controllor;

public abstract class AbstractControllor<S> implements Controllor {

	private S service;

	public S getService() {
		return service;
	}

	public void setService(S service) {
		this.service = service;
	}

	protected void init() {
	}

}
