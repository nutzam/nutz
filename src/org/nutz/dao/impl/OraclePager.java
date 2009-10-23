package org.nutz.dao.impl;

import org.nutz.dao.entity.Entity;

public class OraclePager extends SpecialPager {

	private static String ptn = "SELECT * FROM ("
			+ "SELECT T.*, ROWNUM RN FROM (%%s) T WHERE ROWNUM <= %d) WHERE RN > %d";

	@Override
	public String getSqlPattern(Entity<?> entity) {
		int firstRn = this.getOffset();
		int lastRn = firstRn + this.getPageSize();
		return String.format(ptn, lastRn, firstRn);
	}

}
