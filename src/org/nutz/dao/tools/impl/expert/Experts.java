package org.nutz.dao.tools.impl.expert;

import java.util.regex.Pattern;

import org.nutz.dao.tools.DTable;
import org.nutz.lang.segment.CharSegment;

final class Experts {

	static final String CREATE_PSQL = "CREATE TABLE ${table}(${fields} ${pks})";

	static final String CREATE_MYSQL = CREATE_PSQL + "ENGINE = InnoDB";

	static final String CREATE_ORACLE = CREATE_PSQL;

	static final String CREATE_SQLSERVER = CREATE_PSQL;

	static String gSQL(String ptn, String table, String field) {
		CharSegment cs = new CharSegment(ptn);
		cs.set("T", table).set("F", field);
		return cs.toString();
	}

	static final String PK_ORACLE = "CONSTRAINT ${T}_PK PRIMARY KEY (${F}) ENABLE";

	static final String PK_MYSQL = "PRIMARY KEY (${F})";

	static final String PK_PSQL = "CONSTRAINT ${T}_pkey PRIMARY KEY (${F})";

	static String gPkNames(DTable dt, String ptn, int wrapper) {
		if (!dt.getPks().isEmpty()) {
			StringBuilder sb = new StringBuilder();
			String names = dt.getPkNames((char) wrapper);
			sb.append(Experts.gSQL(ptn, dt.getName(), names));
			return sb.toString();
		}
		return null;
	}

	static boolean isInteger(String type) {
		if (null == type)
			return false;
		type = type.toLowerCase();
		return Pattern.matches("int|long|short|byte|tiny", type);
	}
}
