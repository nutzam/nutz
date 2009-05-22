package com.zzh.mvc.tree;

import com.zzh.service.tree.TreeService;

public class GetDescendants<T> extends TreeControllor<T> {

	public GetDescendants(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.fetchDescendants(obj);
	}

}
