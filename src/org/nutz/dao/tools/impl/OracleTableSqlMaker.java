package org.nutz.dao.tools.impl;

import java.util.Iterator;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.ComboSql;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.TableSqlMaker;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

/**
 * Oracle 的实现，自动增加序列和触发器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class OracleTableSqlMaker extends TableSqlMaker {

	private static String CPK = "CONSTRAINT ${T}_PK PRIMARY KEY (${F}) ENABLE";

	private static String CSEQ = "CREATE SEQUENCE ${T}_${F}_SEQ  MINVALUE 1"
			+ " MAXVALUE 999999999999 INCREMENT BY 1 START" + " WITH 1 CACHE 20 NOORDER  NOCYCLE";
	private static String DSEQ = "DROP SEQUENCE ${T}_${F}_SEQ";

	private static String CTRI = "create or replace trigger ${T}_${F}_ST"
			+ " BEFORE INSERT ON ${T}" + " FOR EACH ROW" + " BEGIN "
			+ " SELECT ${T}_${F}_seq.nextval into :new.id FROM dual;" + " END ${T}_${F}_ST;";

	// private static String DTRI = "DROP trigger ${T}_${F}_seq_trigger";

	private static String gSQL(String ptn, String table, String field) {
		CharSegment cs = new CharSegment(ptn);
		cs.set("T", table).set("F", field);
		return cs.toString();
	}

	@Override
	public Sql makeCreateSql(DTable td) {
		ComboSql sql = new ComboSql();
		// Make create table SQL
		StringBuilder sb = new StringBuilder("CREATE TABLE ").append(td.getName()).append('(');
		appendAllFields(td, sb);
		// Append PK
		Iterator<DField> dfIt = td.getPks().iterator();
		if (dfIt.hasNext()) {
			String names = dfIt.next().getName();
			while (dfIt.hasNext())
				names += "," + dfIt.next();
			sb.append(',').append(gSQL(CPK, td.getName(), names));
		}
		sb.append(')');
		sql.add(Sqls.create(sb.toString()));
		// For all auto increaments fields, create the sequance and trigger
		for (DField df : td.getAutoIncreaments()) {
			// create sequance;
			sql.add(Sqls.create(gSQL(CSEQ, td.getName(), df.getName())));
			// create trigger;
			sql.add(Sqls.create(gSQL(CTRI, td.getName(), df.getName())));
		}
		return sql;
	}

	@Override
	protected void appendField(StringBuilder sb, DField df) {
		appendFieldName(sb, df);
		sb.append(' ').append(getFieldType(df));
		if (!df.isPrimaryKey()) {
			addDecorator(sb, df.isUnique(), " UNIQUE");
			addDecorator(sb, df.isNotNull(), " NOT NULL");
		}
		if (!Strings.isBlank(df.getDefaultValue()))
			sb.append(" DEFAULT ").append(df.getDefaultValue());
	}

	@Override
	protected String getFieldType(DField df) {
		String type = df.getType();
		if ("boolean".equalsIgnoreCase(type))
			return "char(1) check (" + df.getName() + " in(0,1))";
		if ("time".equalsIgnoreCase(type))
			return "TIMESTAMP";
		if ("text".equalsIgnoreCase(type))
			return "VARCHAR(4000)";
		if ("bigint".equalsIgnoreCase(type))
			return "NUMBER";
		return type;
	}

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append(df.getName());
	}

	@Override
	public Sql makeDropSql(DTable td) {
		ComboSql sql = new ComboSql();
		sql.add(Sqls.create("DROP TABLE " + td.getName()));
		for (DField df : td.getAutoIncreaments()) {
			sql.add(Sqls.create(gSQL(DSEQ, td.getName(), df.getName())));
			// sql.addSQL(new ExecutableSql(gSQL(DTRI, td.getName(),
			// df.getName())));
		}
		return sql;
	}

}
