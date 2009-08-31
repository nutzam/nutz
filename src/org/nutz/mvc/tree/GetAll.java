package org.nutz.mvc.tree;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.SimpleCondition;
import org.nutz.mvc.Controllor;
import org.nutz.service.tree.TreeService;

public class GetAll<T> implements Controllor {

	public GetAll(TreeService<T> service) {
		this.service = service;
	}

	private TreeService<T> service;

	private List<T> cacheList;

	public String root;

	public boolean cache;

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (null != cacheList)
			return cacheList;
		List<T> roots = service.query(new SimpleCondition(root), null);
		for (Iterator<T> it = roots.iterator(); it.hasNext();)
			service.fetchDescendants(it.next());
		if (cache) {
			cacheList = roots;
		}
		return roots;
	}

}
