package com.zzh.dao;

import java.util.List;

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
