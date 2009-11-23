package org.nutz.dao.tools;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.tools.impl.TableDefinitionImpl;
import org.nutz.dao.tools.impl.expert.*;
import org.nutz.lang.Lang;

public abstract class Tables {

	public static TableDefinition newInstance(DatabaseMeta db) {
		if (db.isOracle()) {
			return new TableDefinitionImpl(new OracleExpert());
		} else if (db.isMySql()) {
			return new TableDefinitionImpl(new MysqlExpert());
		} else if (db.isPostgresql()) {
			return new TableDefinitionImpl(new PsqlExpert());
		} else if (db.isSqlServer()) {
			return new TableDefinitionImpl(new SqlServerExpert());
		}
		throw Lang.makeThrow("I don't now how to create table for '%s'", db.toString());
	}

}
