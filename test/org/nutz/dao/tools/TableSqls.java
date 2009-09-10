package org.nutz.dao.tools;

import org.nutz.dao.Database;
import org.nutz.dao.ExecutableSql;

public abstract class TableSqls {

	public static TableSqls newInstance(Database db) {
		return null;
	}

	public abstract ExecutableSql makeCreateSql(DTable td);

	public abstract ExecutableSql makeDropSql(DTable td);

}
