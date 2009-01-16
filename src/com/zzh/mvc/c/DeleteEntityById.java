package com.zzh.mvc.c;

import com.zzh.service.IdEntityService;

public class DeleteEntityById extends Action<IdEntityService<?>> {

	private long id;

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public Object execute() throws Exception {
		try {
			getService().delete(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
