package com.zzh.mvc.tree;

import com.zzh.service.tree.TreeService;

public class GetAncestors<T> extends TreeControllor<T> {

	public GetAncestors(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.getAncestors(obj);
	}

}
