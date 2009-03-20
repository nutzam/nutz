package com.zzh.mvc.entity;

import com.zzh.lang.Mirror;
import com.zzh.service.EntityService;

public class Fetch<T> extends EntityAction<T> {

	public Fetch(EntityService<T> service) {
		super(service);
	}

	public String[] ones;

	public String[] manys;

	@Override
	protected Object execute(long id) {
		Object re = Mirror.me(service.getClass()).invoke(service, "fetch", id);
		service.dao().fetchOne(re, ones);
		service.dao().fetchMany(re, manys);
		return re;
	}

	@Override
	protected Object execute(String name) {
		Object re = Mirror.me(service.getClass()).invoke(service, "fetch", name);
		service.dao().fetchOne(re, ones);
		service.dao().fetchMany(re, manys);
		return re;
	}

}
