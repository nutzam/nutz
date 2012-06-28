package org.nutz.dao.pager;

class LoopScope {

    static LoopScope eval(Pager pager, int len) {
        LoopScope ls = new LoopScope();
        pager.setRecordCount(len);
        ls.start = pager.getOffset();
        ls.max = ls.start + pager.getPageSize();
        return ls;
    }

    /**
     * 起始，一般由 0 开始
     */
    public int start; // inclusive
    /**
     * 结束，不包括
     */
    public int max; // exclusive

    @Override
    public String toString() {
        return "[" + start + "," + max + "]";
    }

}
