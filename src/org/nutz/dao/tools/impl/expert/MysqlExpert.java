package org.nutz.dao.tools.impl.expert;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.impl.SqlExpert;
import org.nutz.lang.Strings;

public class MysqlExpert implements SqlExpert {

	public Sql evalCreateSql(DTable dt, Sql createTable) {
		return createTable;
	}

	public Sql evalDropSql(DTable dt, Sql dropTable) {
		return dropTable;
	}

	public String tellCreateSqlPattern() {
		return Experts.CREATE_MYSQL;
	}

	private void appendFieldType(StringBuilder sb, DField df) {
		sb.append(df.getType());
	}

	public String tellField(int pkNum, DField df) {
		StringBuilder sb = new StringBuilder();
		// Name
		sb.append('`').append(df.getName()).append('`').append(' ');
		// Type
		appendFieldType(sb, df);
		// Decorator
		if (Experts.isInteger(df.getType()) && df.isUnsign())
			sb.append(" UNSIGNED");
		if (!df.isPrimaryKey() && df.isUnique())
			sb.append(" UNIQUE");
		if (df.isNotNull())
			sb.append(" NOT NULL");
		if (df.isAutoIncreament())
			sb.append(" AUTO_INCREMENT");
		// Default Value
		if (!Strings.isBlank(df.getDefaultValue()))
			sb.append(" DEFAULT ").append(df.getDefaultValue());
		return sb.toString();
	}

	public String tellPKs(DTable dt) {
		return Experts.gPkNames(dt, Experts.PK_MYSQL, '`');
	}

}
