package org.nutz.dao.sql;

public class SqlLiteral {

	public SqlLiteral setVar(String name, Object value) {
		return this;
	}

	public SqlLiteral valueOf(String str) {
		return this;
	}

	public String toPrepareStatementString() {
		return null;
	}

}
