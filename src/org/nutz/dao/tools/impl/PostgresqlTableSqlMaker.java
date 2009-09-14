package org.nutz.dao.tools.impl;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.TableSqlMaker;

public class PostgresqlTableSqlMaker extends TableSqlMaker {

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append(df.getName());
	}
}
