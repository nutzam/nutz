package org.nutz.dao.pager;


class LoopScope {

	static LoopScope eval(Pager pager, int len) {
		LoopScope ls = new LoopScope();
		pager.setRecordCount(len);
		ls.start = pager.getOffset();
		ls.max = ls.start + pager.getPageSize();
		return ls;
	}

	public int start; // inclusive
	public int max; // exclusive

	@Override
	public String toString() {
		return "[" + start + "," + max + "]";
	}

}
