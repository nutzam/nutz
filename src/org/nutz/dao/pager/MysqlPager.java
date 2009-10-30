package org.nutz.dao.pager;

public class MysqlPager extends AbstractPager {

	public String format(String sql) {
		StringBuilder sb = new StringBuilder(sql);
		sb.append(" LIMIT ").append(getOffset()).append(',').append(getPageSize());
		return sb.toString();
	}

}
