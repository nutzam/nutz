package org.nutz.dao.pager;

import org.nutz.dao.DatabaseMeta;

public class DefaultPagerMaker implements PagerMaker {

	public Pager make(DatabaseMeta meta, int pageNumber, int pageSize) {
		Pager pager;
		// MySql
		if (meta.isMySql()) {
			pager = new MysqlPager();
		}
		// Postgresql
		else if (meta.isPostgresql()) {
			pager = new PostgresqlPager();
		}
		// Oracle
		else if (meta.isOracle()) {
			pager = new OraclePager();
		}
		// SqlServer
		else if (meta.isSqlServer()) {
			pager = new SqlServer2005Pager();
		}
		// DB2
		else if (meta.isDB2()) {
			pager = new DB2Pager();
		}
		// Unknown
		else {
			pager = new UnknownPager();
		}
		pager.setPageNumber(pageNumber);
		pager.setPageSize(pageSize);
		return pager;
	}

}
