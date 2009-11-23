package org.nutz.dao.tools;

import org.nutz.dao.sql.Sql;

public interface TableDefinition {

	public abstract Sql makeCreateSql(DTable dt);

	public abstract Sql makeDropSql(DTable dt);

}