package org.nutz.dao;

public class DatabaseMeta {

	public static final String DB2 = "db2";
	public static final String PSQL = "psql";
	public static final String ORACLE = "oracle";
	public static final String SQLSERVER = "sqlserver";
	public static final String MYSQL = "mysql";
	public static final String UNKNOWN = "unknown";

	private static enum TYPE {
		DB2, PSQL, ORACLE, SQLSERVER, MYSQL, UNKNOWN
	}

	public DatabaseMeta() {
		type = TYPE.UNKNOWN;
	}

	private TYPE type;

	private String version;

	private String productName;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
		String proName = productName.toLowerCase();
		if (proName.startsWith("postgresql")) {
			this.type = TYPE.PSQL;
		} else if (proName.startsWith("mysql")) {
			this.type = TYPE.MYSQL;
		} else if (proName.startsWith("oracle")) {
			this.type = TYPE.ORACLE;
		} else if (proName.startsWith("db2")) {
			this.type = TYPE.DB2;
		} else if (proName.startsWith("microsoft sql")) {
			this.type = TYPE.SQLSERVER;
		} else {
			this.type = TYPE.UNKNOWN;
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setAsMysql() {
		this.type = TYPE.MYSQL;
	}

	public void setAsPsql() {
		this.type = TYPE.PSQL;
	}

	public void setAsOracle() {
		this.type = TYPE.ORACLE;
	}

	public void setAsSqlServer() {
		this.type = TYPE.SQLSERVER;
	}

	public void setAsDB2() {
		this.type = TYPE.DB2;
	}

	public void setUnknown() {
		this.type = TYPE.UNKNOWN;
	}

	public boolean is(String typeName) {
		return type.name().equalsIgnoreCase(typeName);
	}

	public String getTypeName() {
		return type.name();
	}

	public boolean isUnknown() {
		return TYPE.UNKNOWN == type;
	}

	public boolean isMySql() {
		return TYPE.MYSQL == type;
	}

	public boolean isPostgresql() {
		return TYPE.PSQL == type;
	}

	public boolean isSqlServer() {
		return TYPE.SQLSERVER == type;
	}

	public boolean isOracle() {
		return TYPE.ORACLE == type;
	}

	public boolean isDB2() {
		return TYPE.DB2 == type;
	}

}
