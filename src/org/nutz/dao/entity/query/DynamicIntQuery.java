package org.nutz.dao.entity.query;

import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.IntQuery;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.segment.Segment;

class DynamicIntQuery implements IntQuery {

	private Segment seg;

	DynamicIntQuery(Segment seg) {
		this.seg = seg;
	}

	public Sql sql() {
		return Sqls.fetchInt((TableName.render(seg)));
	}

}
