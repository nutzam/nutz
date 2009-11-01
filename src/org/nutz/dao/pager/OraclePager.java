package org.nutz.dao.pager;

import org.nutz.dao.entity.Entity;

public class OraclePager extends AbstractPager {

	private static String PTN =
	// <min>
	"SELECT * FROM ("
	// <..max>
			+ "SELECT T.*, ROWNUM RN FROM ("
			// <...query>
			+ "SELECT %s FROM %s %s"
			// </...query>
			+ ") T WHERE ROWNUM <= %d)"
			// </..max>
			+ " WHERE RN > %d"; // </min>

	public String toSql(Entity<?> entity, String fields, String cnd) {
		int from = this.getOffset();
		int to = from + this.getPageSize();
		return String.format(PTN, fields, entity.getViewName(), cnd, to, from);
	}

}
