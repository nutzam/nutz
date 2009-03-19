package com.zzh.dao.impl;

import com.zzh.dao.Pager;
import com.zzh.lang.Lang;

public class OraclePager extends Pager {

	private static String ptn = "SELECT * FROM ("
			+ "SELECT T.*, ROWNUM RN FROM (%%s) T WHERE ROWNUM <= %d) WHERE RN > %d";

	protected OraclePager() {
		throw Lang.makeThrow("No implement yet!!!");
	}

	@Override
	protected String getLimitString() {
		int firstRn = this.getOffset();
		int lastRn = firstRn + this.getPageSize();
		return String.format(ptn, lastRn, firstRn);
	}

}
