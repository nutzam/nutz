package org.nutz.mvc.tree;

import org.nutz.service.tree.TreeService;

public class GetAncestors<T> extends TreeControllor<T> {

	public GetAncestors(TreeService<T> service) {
		super(service);
	}

	@Override
	protected Object execute(T obj) {
		return service.getAncestors(obj);
	}

}
