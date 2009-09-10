package org.nutz.dao.tools;

import java.util.Iterator;

import org.nutz.dao.Database;
import org.nutz.dao.ExecutableSql;
import org.nutz.dao.Sql;
import org.nutz.lang.Strings;

public abstract class TableSqlMaker {

	public static TableSqlMaker newInstance(Database db) {
		return null;
	}

	protected static void addDecorator(StringBuilder sb, boolean flag, String word) {
		if (flag) {
			sb.append(word);
		}
	}

	public Sql<?> makeCreateSql(DTable td) {
		StringBuilder sb = new StringBuilder("CREATE TABLE ").append(td.getName()).append('(');
		addAllFields(td, sb);
		sb.append(')');
		return new ExecutableSql(sb.toString());
	}

	protected void addAllFields(DTable td, StringBuilder sb) {
		Iterator<DField> it = td.getFields().iterator();
		if (it.hasNext()) {
			appendField(sb, it.next());
			while (it.hasNext())
				appendField(sb.append(','), it.next());
		}
	}

	protected void appendField(StringBuilder sb, DField df) {
		sb.append(df.getName());
		sb.append(' ').append(df.getType());
		addDecorator(sb, df.isUnsign(), " UNSIGNED");
		addDecorator(sb, df.isAutoIncreament(), " SERIAL");
		addDecorator(sb, df.isPrimaryKey(), " PRIMARY KEY");
		addDecorator(sb, df.isUnique(), " UNIQUE");
		addDecorator(sb, df.isNotNull(), " NOT NULL");
		if (Strings.isBlank(df.getDefaultValue()))
			sb.append(" DEFAULT ").append(df.getDefaultValue());
	}

	public Sql<?> makeDropSql(DTable td) {
		return new ExecutableSql(String.format("DROP TABLE IF EXISTS %s", td.getName()));
	}
}
