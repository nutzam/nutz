package org.nutz.dao;

import javax.servlet.ServletRequest;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.*;
import org.nutz.lang.Lang;

public abstract class Pager {
	/*----------------------------------------------------------------*/

	public static final Class<? extends Pager> DB2 = DB2Pager.class;
	public static final Class<? extends Pager> MySQL = MySQLPager.class;
	public static final Class<? extends Pager> Postgresql = PostgresqlPager.class;
	public static final Class<? extends Pager> Oracle = OraclePager.class;
	public static final Class<? extends Pager> SQLServer = SQLServerPager.class;

	/*----------------------------------------------------------------*/
	public static <T extends Pager> Pager create(Class<T> type, int pageNumber, int pageSize) {
		Pager p;
		if (null == type) {
			p = new Pager() {
				protected String getLimitString(Entity<?> entity) {
					return null;
				}
			};
		} else {
			try {
				p = type.newInstance();
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		p.setPageNumber(pageNumber);
		p.setPageSize(pageSize);
		return p;
	}

	public static <T extends Pager> Pager valueOf(ServletRequest request, Class<T> type) {
		try {
			int pn = Integer.parseInt(request.getParameter("pn"));
			if (pn > 0) {
				int size;
				try {
					size = Integer.parseInt(request.getParameter("pagesize"));
				} catch (Exception e) {
					size = Pager.DEFAULT_PAGE_SIZE;
				}
				return create(type, pn, size);
			}
		} catch (Exception e) {}
		return null;
	}

	private static final int DEFAULT_PAGE_SIZE = 20;
	private static final int FIRST_PAGE_NUMBER = 1;

	protected Pager() {}

	private int pageNumber;
	private int pageSize;
	private int pageCount;
	private int recordCount;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("pn:");
		sb.append(this.pageNumber);
		sb.append(" | pagesize:");
		sb.append(this.pageSize);
		sb.append(" | pnum:");
		sb.append(this.getPageCount());
		sb.append(" | rnum:");
		sb.append(this.getRecordCount());
		return sb.toString();
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = (pageNumber > FIRST_PAGE_NUMBER ? pageNumber : FIRST_PAGE_NUMBER);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = (pageSize > 0 ? pageSize : Pager.DEFAULT_PAGE_SIZE);
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = (recordCount > 0 ? recordCount : 0);
		this.pageCount = (int) Math.ceil((double) recordCount / pageSize);
	}

	public int getOffset() {
		return pageSize * (pageNumber - 1);
	}

	protected abstract String getLimitString(Entity<?> entity);

	public String getResultSetName(Entity<?> entity) {
		return null;
	}

	public boolean isDefault() {
		return true;
	}
}
