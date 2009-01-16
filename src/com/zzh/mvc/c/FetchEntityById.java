package com.zzh.mvc.c;

import com.zzh.service.IdEntityService;

public class FetchEntityById extends Action<IdEntityService<?>> {

	private long id;

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public Object execute() throws Exception {
		return getService().fetch(id);
	}

}
