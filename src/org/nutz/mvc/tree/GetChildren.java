package org.nutz.mvc.tree;

import org.nutz.service.tree.TreeService;

public class GetChildren<T> extends TreeControllor<T> {

	public GetChildren(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.fetchChildrenOnly(obj);
	}

}
