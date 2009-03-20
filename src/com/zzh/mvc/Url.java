package com.zzh.mvc;

public class Url {

	private View error;

	private View ok;

	private Controllor[] controllors;

	public View getOk() {
		return ok;
	}

	public void setOk(View view) {
		this.ok = view;
	}

	public View getError() {
		return error;
	}

	public void setError(View error) {
		this.error = error;
	}

	public Controllor[] getControllors() {
		return controllors;
	}

	public void setControllors(Controllor[] controllors) {
		this.controllors = controllors;
	}

}
