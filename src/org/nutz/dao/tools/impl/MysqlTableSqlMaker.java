package org.nutz.dao.tools.impl;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.TableSqlMaker;

public class MysqlTableSqlMaker extends TableSqlMaker {

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append('`').append(df.getName()).append('`');
	}

	@Override
	protected void appendSqlEnd(StringBuilder sb) {
		sb.append(")ENGINE = InnoDB");
	}

}
