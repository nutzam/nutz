package com.zzh.mvc.tree;

import com.zzh.service.tree.TreeService;

public class getDescendants<T> extends TreeControllor<T> {

	public getDescendants(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.fetchDescendants(obj);
	}

}
