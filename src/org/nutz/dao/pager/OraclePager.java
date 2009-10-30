package org.nutz.dao.pager;

public class OraclePager extends AbstractPager {

	public String format(String sql) {
		int firstRn = this.getOffset();
		int lastRn = firstRn + this.getPageSize();
		StringBuilder sb = new StringBuilder("SELECT * FROM (SELECT T.*, ROWNUM RN FROM (");
		sb.append(sql);
		sb.append(") T WHERE ROWNUM <=").append(lastRn);
		sb.append(") WHERE RN >").append(firstRn);
		return sb.toString();
	}

}
