package org.nutz.dao.entity.query;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.IntQuery;
import org.nutz.dao.sql.Sql;

class StaticIntQuery implements IntQuery {
	
	private Sql sql;

	StaticIntQuery(String s) {
		sql = Sqls.fetchInt(s);
	}

	public Sql sql() {
		return sql;
	}

}
