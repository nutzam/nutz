package com.zzh.mvc.entity;

import com.zzh.lang.Mirror;
import com.zzh.mvc.Return;
import com.zzh.service.EntityService;

public class Delete<T> extends EntityAction<T> {

	public Delete(EntityService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(long id) {
		Mirror.me(service.getClass()).invoke(service, "delete", id);
		return Return.OK();
	}

	@Override
	protected Object execute(String name) {
		Mirror.me(service.getClass()).invoke(service, "delete", name);
		return Return.OK();
	}

}
