package org.nutz.dao.tools.impl;

import java.util.Iterator;

import org.nutz.dao.ComboSql;
import org.nutz.dao.ExecutableSql;
import org.nutz.dao.Sql;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.TableSqlMaker;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;

public class OracleTableSqlMaker extends TableSqlMaker {

	private static String CPK = "CONSTRAINT ${T}_PK PRIMARY KEY (${F}) ENABLE";

	private static String CSEQ = "CREATE SEQUENCE ${T}_${F}_SEQ  MINVALUE 1"
			+ " MAXVALUE 999999999999 INCREMENT BY 1 START" + " WITH 21 CACHE 20 NOORDER  NOCYCLE";
	private static String DSEQ = "DROP SEQUENCE ${T}_${F}_SEQ";

	private static String CTRI = "create or replace trigger ${T}_${F}_seq_trigger"
			+ " BEFORE INSERT ON ${T}" + " FOR EACH ROW" + " BEGIN "
			+ " SELECT ${T}_${F}_seq.nextval into :new.id FROM dual;"
			+ " END ${T}_${F}_seq_trigger;";

	// private static String DTRI = "DROP trigger ${T}_${F}_seq_trigger";

	private static String gSQL(String ptn, String table, String field) {
		CharSegment cs = new CharSegment(ptn);
		cs.set("T", table).set("F", field);
		return cs.toString();
	}

	@Override
	public Sql<?> makeCreateSql(DTable td) {
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
		sql.addSQL(new ExecutableSql(sb.toString()));
		// For all auto increaments fields, create the sequance and trigger
		for (DField df : td.getAutoIncreaments()) {
			// create sequance;
			sql.addSQL(new ExecutableSql(gSQL(CSEQ, td.getName(), df.getName())));
			// create trigger;
			sql.addSQL(new ExecutableSql(gSQL(CTRI, td.getName(), df.getName())));
		}
		return sql;
	}

	@Override
	protected void appendField(StringBuilder sb, DField df) {
		appendFieldName(sb, df);
		sb.append(' ').append(df.getType());
		if (!df.isPrimaryKey()) {
			addDecorator(sb, df.isUnique(), " UNIQUE");
			addDecorator(sb, df.isNotNull(), " NOT NULL");
		}
		if (!Strings.isBlank(df.getDefaultValue()))
			sb.append(" DEFAULT ").append(df.getDefaultValue());
	}

	protected void appendFieldName(StringBuilder sb, DField df) {
		sb.append(df.getName());
	}

	@Override
	public Sql<?> makeDropSql(DTable td) {
		ComboSql sql = new ComboSql();
		sql.addSQL(new ExecutableSql("DROP TABLE " + td.getName()));
		for (DField df : td.getAutoIncreaments()) {
			sql.addSQL(new ExecutableSql(gSQL(DSEQ, td.getName(), df.getName())));
			// sql.addSQL(new ExecutableSql(gSQL(DTRI, td.getName(),
			// df.getName())));
		}
		return sql;
	}

}
