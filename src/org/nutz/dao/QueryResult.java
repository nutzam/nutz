package org.nutz.dao;

import java.util.List;

import org.nutz.dao.pager.Pager;

public class QueryResult {

	private List<?> list;
	private Pager pager;

	public QueryResult(List<?> list, Pager pager) {
		this.list = list;
		this.pager = pager;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

}
