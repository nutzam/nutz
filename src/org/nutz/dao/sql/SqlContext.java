package org.nutz.dao.sql;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.pager.Pager;

public class SqlContext {

	private FieldMatcher matcher;
	private Pager pager;
	private Object result;

	public Pager getPager() {
		return pager;
	}

	public SqlContext setPager(Pager pager) {
		this.pager = pager;
		return this;
	}

	public FieldMatcher getMatcher() {
		return matcher;
	}

	public SqlContext setMatcher(FieldMatcher matcher) {
		this.matcher = matcher;
		return this;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

}
