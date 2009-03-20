package com.zzh.mvc.tree;

import com.zzh.service.tree.TreeService;

public class getAncestors<T> extends TreeControllor<T> {

	public getAncestors(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.getAncestors(obj);
	}

}
