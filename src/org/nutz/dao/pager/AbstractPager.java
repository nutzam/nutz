package org.nutz.dao.pager;

import java.sql.ResultSet;

import org.nutz.dao.pager.Pager;

public abstract class AbstractPager implements Pager {

	static final int DEFAULT_PAGE_SIZE = 20;
	static final int FIRST_PAGE_NUMBER = 1;

	private int pageNumber;
	private int pageSize;
	private int pageCount;
	private int recordCount;

	public Pager resetPageCount() {
		pageCount = -1;
		return this;
	}

	public int getPageCount() {
		if (pageCount < 0)
			pageCount = (int) Math.ceil((double) recordCount / pageSize);
		return pageCount;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public Pager setPageNumber(int pn) {
		pageNumber = pn > FIRST_PAGE_NUMBER ? pn : FIRST_PAGE_NUMBER;
		return this;
	}

	public Pager setPageSize(int pageSize) {
		this.pageSize = (pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
		return resetPageCount();
	}

	public Pager setRecordCount(int recordCount) {
		this.recordCount = recordCount > 0 ? recordCount : 0;
		return resetPageCount();
	}

	public int getOffset() {
		return pageSize * (pageNumber - 1);
	}

	public int getResultSetType() {
		return ResultSet.TYPE_FORWARD_ONLY;
	}

}
