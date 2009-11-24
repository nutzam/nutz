package org.nutz.dao;

/**
 * 描述了一个数据源的元数据
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class DatabaseMeta {

	public DatabaseMeta() {
		type = DatabaseType.UNKNOWN;
	}

	/**
	 * 现在所支持的数据库类型
	 */
	private DatabaseType type;

	/**
	 * 产品版本号
	 */
	private String version;

	/**
	 * 产品名称
	 */
	private String productName;

	public String getProductName() {
		return productName;
	}

	public String toString() {
		return String.format("%s:[%s - %s]", type.name(), productName, version);
	}

	public void setProductName(String productName) {
		this.productName = productName;
		String proName = productName.toLowerCase();
		if (proName.startsWith("postgresql")) {
			this.type = DatabaseType.PSQL;
		} else if (proName.startsWith("mysql")) {
			this.type = DatabaseType.MYSQL;
		} else if (proName.startsWith("oracle")) {
			this.type = DatabaseType.ORACLE;
		} else if (proName.startsWith("db2")) {
			this.type = DatabaseType.DB2;
		} else if (proName.startsWith("microsoft sql")) {
			this.type = DatabaseType.SQLSERVER;
		} else {
			this.type = DatabaseType.UNKNOWN;
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setAsMysql() {
		this.type = DatabaseType.MYSQL;
	}

	public void setAsPsql() {
		this.type = DatabaseType.PSQL;
	}

	public void setAsOracle() {
		this.type = DatabaseType.ORACLE;
	}

	public void setAsSqlServer() {
		this.type = DatabaseType.SQLSERVER;
	}

	public void setAsDB2() {
		this.type = DatabaseType.DB2;
	}

	public void setUnknown() {
		this.type = DatabaseType.UNKNOWN;
	}

	public boolean is(String typeName) {
		return type.name().equalsIgnoreCase(typeName);
	}

	public String getTypeName() {
		return type.name();
	}

	public boolean isUnknown() {
		return DatabaseType.UNKNOWN == type;
	}

	public boolean isMySql() {
		return DatabaseType.MYSQL == type;
	}

	public boolean isPostgresql() {
		return DatabaseType.PSQL == type;
	}

	public boolean isSqlServer() {
		return DatabaseType.SQLSERVER == type;
	}

	public boolean isOracle() {
		return DatabaseType.ORACLE == type;
	}

	public boolean isDB2() {
		return DatabaseType.DB2 == type;
	}

}
