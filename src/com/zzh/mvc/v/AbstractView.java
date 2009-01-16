package com.zzh.mvc.v;

import com.zzh.mvc.View;

public abstract class AbstractView implements View {

	protected String name;

	public AbstractView() {
	}

	public AbstractView(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}