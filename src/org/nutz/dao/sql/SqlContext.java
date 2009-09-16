package org.nutz.dao.sql;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Pager;

public class SqlContext {

	private FieldMatcher matcher;
	private Pager pager;

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

}
