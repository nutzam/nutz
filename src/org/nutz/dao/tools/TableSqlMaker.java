package org.nutz.dao.tools;

import java.util.Iterator;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.impl.MysqlTableSqlMaker;
import org.nutz.dao.tools.impl.OracleTableSqlMaker;
import org.nutz.dao.tools.impl.PostgresqlTableSqlMaker;
import org.nutz.lang.Strings;

public abstract class TableSqlMaker {

	public static TableSqlMaker newInstance(DatabaseMeta db) {
		if (db.isOracle()) {
			return new OracleTableSqlMaker();
		} else if (db.isMySql()) {
			return new MysqlTableSqlMaker();
		} else if (db.isPostgresql()) {
			return new PostgresqlTableSqlMaker();
		}
		return new TableSqlMaker() {

			@Override
			protected void appendFieldName(StringBuilder sb, DField df) {
				sb.append(df.getName());
			}

		};
	}

	protected static void addDecorator(StringBuilder sb, boolean flag, String word) {
		if (flag) {
			sb.append(word);
		}
	}

	public Sql makeCreateSql(DTable dt) {
		StringBuilder sb = new StringBuilder();
		appendBegin(dt, sb);
		appendAllFields(dt, sb);
		appendSqlEnd(sb);
		return Sqls.create(sb.toString());
	}

	protected void appendBegin(DTable dt, StringBuilder sb) {
		sb.append("CREATE TABLE ").append(dt.getName()).append('(');
	}

	protected void appendSqlEnd(StringBuilder sb) {
		sb.append(')');
	}

	protected void appendAllFields(DTable td, StringBuilder sb) {
		Iterator<DField> it = td.getFields().iterator();
		if (it.hasNext()) {
			appendField(sb, it.next());
			while (it.hasNext())
				appendField(sb.append(",\n"), it.next());
		}
	}

	protected void appendField(StringBuilder sb, DField df) {
		appendFieldName(sb, df);
		addDecorator(sb, df.isUnsign(), " UNSIGNED");
		if (df.isAutoIncreament() && df.isPrimaryKey()) {
			sb.append(" SERIAL PRIMARY KEY");
		} else {
			sb.append(' ').append(getFieldType(df));
			addDecorator(sb, df.isPrimaryKey(), " PRIMARY KEY");
			addDecorator(sb, df.isUnique(), " UNIQUE");
			addDecorator(sb, df.isNotNull(), " NOT NULL");
			if (!Strings.isBlank(df.getDefaultValue()))
				sb.append(" DEFAULT ").append(df.getDefaultValue());
		}
	}

	protected String getFieldType(DField df) {
		return df.getType();
	}

	protected abstract void appendFieldName(StringBuilder sb, DField df);

	public Sql makeDropSql(DTable dt) {
		return Sqls.create(String.format("DROP TABLE IF EXISTS %s", dt.getName()));
	}
}
