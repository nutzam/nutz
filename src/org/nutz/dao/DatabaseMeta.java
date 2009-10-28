package org.nutz.dao;

public class DatabaseMeta {

	public static final String DB2 = "db2";
	public static final String PSQL = "psql";
	public static final String ORACLE = "oracle";
	public static final String SQLSERVER = "sqlserver";
	public static final String MYSQL = "mysql";
	public static final String UNKNOWN = "unknown";

	public DatabaseMeta() {
		productName = UNKNOWN;
		pagerType = Pager.class;
	}

	private String url;

	private String userName;

	private Class<? extends Pager> pagerType;

	private String productName;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Pager createPager(int pageNumber, int pageSize) {
		return Pager.create(pagerType, pageNumber, pageSize);
	}

	public Class<? extends Pager> getPagerType() {
		return pagerType;
	}

	public void setPagerType(Class<? extends Pager> pagerType) {
		this.pagerType = pagerType;
	}

	public boolean isUnknown() {
		return Pager.class == pagerType;
	}

	public boolean isMySql() {
		return Pager.MySQL == pagerType;
	}

	public boolean isPostgresql() {
		return Pager.Postgresql == pagerType;
	}

	public boolean isSqlServer() {
		return Pager.SQLServer == pagerType;
	}

	public boolean isOracle() {
		return Pager.Oracle == pagerType;
	}

	public boolean isDB2() {
		return Pager.DB2 == pagerType;
	}

}
