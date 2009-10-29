package org.nutz.dao.tools.impl;

import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.TableSqlMaker;

/**
 * Microsoft SQLServer's Table SQL Maker
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SqlServerTableSqlMaker extends TableSqlMaker {

	/**
	 * 如何增加自增长的字段：
	 * 
	 * <pre>
	 * create table t_abc(
	 * id int primary   key   identity,
	 * name varchar(20) unique
	 * );
	 * </pre>
	 */
	protected void appendAutoIncreamentPK(StringBuilder sb, DField df) {
		appendFieldName(sb, df);
		appendFieldType(sb, df);
		addDecorator(sb, df.isUnsign(), " UNSIGNED");
		sb.append(" PRIMARY KEY IDENTITY");
	}

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append('"').append(df.getName()).append('"');
	}

	@Override
	protected void appendFieldType(StringBuilder sb, DField df) {
		sb.append(' ');
		// BOOLEAN
		if ("boolean".equalsIgnoreCase(df.getType())) {
			sb.append("BIT");
		}
		// Date
		else if ("date".equalsIgnoreCase(df.getType())) {
			sb.append("DATETIME");
		}
		// Time
		else if ("time".equalsIgnoreCase(df.getType())) {
			sb.append("DATETIME");
		}
		// Timestamp
		else if ("timestamp".equalsIgnoreCase(df.getType())) {
			sb.append("DATETIME");
		}
		// Others
		else {
			sb.append(df.getType());
		}
	}

}
