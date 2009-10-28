package org.nutz.dao.tools.impl;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.TableSqlMaker;

/**
 * 最标准的实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class PostgresqlTableSqlMaker extends TableSqlMaker {

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append(df.getName());
	}
}
