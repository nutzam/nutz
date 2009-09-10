package org.nutz.dao.tools.impl;

import org.nutz.dao.ExecutableSql;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.TableSqlMaker;

public class OracleTableSqlMaker extends TableSqlMaker {

	@Override
	public ExecutableSql makeCreateSql(DTable td) {
		return null;
	}

	@Override
	public ExecutableSql makeDropSql(DTable td) {
		return null;
	}

}
