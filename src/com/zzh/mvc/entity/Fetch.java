package com.zzh.mvc.entity;

import com.zzh.service.EntityService;

public class Fetch<T> extends EntityAction<T> {

	public Fetch(EntityService<T> service) {
		super(service);
	}

	private String cascade;

	@Override
	protected Object execute(long id) {
		Object re = service.dao().fetch(service.getEntity(), id);
		service.dao().fetchLinks(re, cascade);
		return re;
	}

	@Override
	protected Object execute(String name) {
		Object re = service.dao().fetch(service.getEntity(), name);
		service.dao().fetchLinks(re, cascade);
		return re;
	}

}
