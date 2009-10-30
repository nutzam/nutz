package org.nutz.dao.pager;

public class PostgresqlPager extends AbstractPager {

	public String format(String sql) {
		StringBuilder sb = new StringBuilder(sql);
		sb.append(" LIMIT ").append(getPageSize()).append(" OFFSET ").append(getOffset());
		return sb.toString();
	}

}
