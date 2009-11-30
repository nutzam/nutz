package org.nutz.dao.tools.impl.expert;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.ComboSql;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.impl.SqlExpert;
import org.nutz.lang.Strings;

public class OracleExpert implements SqlExpert {

	private static String CSEQ = "CREATE SEQUENCE ${T}_${F}_SEQ  MINVALUE 1"
			+ " MAXVALUE 999999999999 INCREMENT BY 1 START" + " WITH 1 CACHE 20 NOORDER  NOCYCLE";
	private static String DSEQ = "DROP SEQUENCE ${T}_${F}_SEQ";

	private static String CTRI = "create or replace trigger ${T}_${F}_ST"
			+ " BEFORE INSERT ON ${T}" + " FOR EACH ROW" + " BEGIN "
			+ " SELECT ${T}_${F}_seq.nextval into :new.${F} FROM dual;" + " END ${T}_${F}_ST;";

	public Sql evalCreateSql(DTable dt, Sql createTable) {
		ComboSql sql = new ComboSql();
		sql.add(createTable);
		for (DField df : dt.getAutoIncreaments()) {
			// 序列
			sql.add(Sqls.create(Experts.gSQL(CSEQ, dt.getName(), df.getName())));
			// 触发器
			sql.add(Sqls.create(Experts.gSQL(CTRI, dt.getName(), df.getName())));
		}
		return sql;
	}

	public Sql evalDropSql(DTable dt, Sql dropTable) {
		ComboSql sql = new ComboSql();
		sql.add(dropTable);
		for (DField df : dt.getAutoIncreaments()) {
			sql.add(Sqls.create(Experts.gSQL(DSEQ, dt.getName(), df.getName())));
		}
		return sql;
	}

	public String tellCreateSqlPattern() {
		return Experts.CREATE_ORACLE;
	}

	private void appendFieldType(StringBuilder sb, DField df) {
		sb.append(' ');
		// BOOLEAN
		if ("boolean".equalsIgnoreCase(df.getType())) {
			sb.append("char(1) check (" + df.getName() + " in(0,1))");
		}
		// Time
		else if ("time".equalsIgnoreCase(df.getType())) {
			sb.append("TIMESTAMP");
		}
		// Text
		else if ("text".equalsIgnoreCase(df.getType())) {
			sb.append("VARCHAR(4000)");
		}
		// Bigint
		else if ("bigint".equalsIgnoreCase(df.getType())) {
			sb.append("NUMBER");
		}
		// Others
		else {
			sb.append(df.getType());
		}
	}

	public String tellField(int pkNum, DField df) {
		StringBuilder sb = new StringBuilder();
		// Name
		sb.append(df.getName()).append(' ');
		// Type
		appendFieldType(sb, df);
		// Decorator
		if (!df.isPrimaryKey() && df.isUnique())
			sb.append(" UNIQUE");
		if (df.isNotNull())
			sb.append(" NOT NULL");
		// Default Value
		if (!Strings.isBlank(df.getDefaultValue()))
			sb.append(" DEFAULT ").append(df.getDefaultValue());
		return sb.toString();
	}

	public String tellPKs(DTable dt) {
		return Experts.gPkNames(dt, Experts.PK_ORACLE, 0);
	}

}
