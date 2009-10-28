package org.nutz.dao.tools.impl;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.TableSqlMaker;

/**
 * 会强制使用 InnoDB 引擎
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MysqlTableSqlMaker extends TableSqlMaker {

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append('`').append(df.getName()).append('`');
	}

	@Override
	protected void appendSqlEnd(StringBuilder sb) {
		sb.append(")ENGINE = InnoDB");
	}

}
