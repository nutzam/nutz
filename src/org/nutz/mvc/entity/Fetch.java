package org.nutz.mvc.entity;

import org.nutz.service.EntityService;

public class Fetch extends EntityAction {

	protected Fetch() {
		super();
	}

	public Fetch(EntityService<?> service) {
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
