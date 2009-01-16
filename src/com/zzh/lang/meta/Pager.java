package com.zzh.lang.meta;

import javax.servlet.ServletRequest;

public final class Pager {
	public static final int DEFAULT_PAGE_SIZE = 20;
	public static final int FIRST_PAGE_NUMBER = 1;

	public Pager() {
		this.pageNumber = FIRST_PAGE_NUMBER;
		this.pageCount = 0;
		this.recordCount = 0;
		this.pageSize = Pager.DEFAULT_PAGE_SIZE;
	}

	private int pageNumber;
	private int pageSize;
	private int pageCount;
	private int recordCount;

	public Pager(int pageNumber, int pageSize) {
		this.pageNumber = (pageNumber > FIRST_PAGE_NUMBER ? pageNumber
				: FIRST_PAGE_NUMBER);
		this.pageCount = 0;
		this.recordCount = 0;
		this.pageSize = (pageSize > 0 ? pageSize : Pager.DEFAULT_PAGE_SIZE);
	}

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
		this.pageNumber = (pageNumber > FIRST_PAGE_NUMBER ? pageNumber
				: FIRST_PAGE_NUMBER);
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

	public int firstItemAbsolutIndex() {
		return pageSize * (pageNumber - 1) + 1;
	}

	public static Pager valueOf(ServletRequest request) {
		Pager pager = null;
		try {
			int pn = Integer.parseInt(request.getParameter("pn"));
			if (pn > 0) {
				int size;
				try {
					size = Integer.parseInt(request.getParameter("pagesize"));
				} catch (Exception e) {
					size = Pager.DEFAULT_PAGE_SIZE;
				}
				pager = new Pager(pn, size);
			}
		} catch (Exception e) {
		}
		return pager;
	}

	public static int firstItemAbsolutIndex(Pager pager) {
		if (null == pager)
			return 1;
		return pager.firstItemAbsolutIndex();
	}
}
