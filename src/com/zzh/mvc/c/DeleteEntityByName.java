package com.zzh.mvc.c;

import com.zzh.service.NameEntityService;

public class DeleteEntityByName extends Action<NameEntityService<?>> {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object execute() throws Exception {
		try {
			((NameEntityService<?>) getService()).delete(name);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
