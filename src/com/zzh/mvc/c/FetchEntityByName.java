package com.zzh.mvc.c;

import com.zzh.service.NameEntityService;

public class FetchEntityByName extends Action<NameEntityService<?>> {

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object execute() throws Exception {
		return getService().fetch(name);
	}

}
