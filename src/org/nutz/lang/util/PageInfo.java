package org.nutz.lang.util;

public interface PageInfo {
    /**
     * 一共有多少页
     */
    int getPageCount();

    /**
     * 当前是第几页， 从 1 开始
     */
    int getPageNumber();

    /**
     * 设置页码
     */
    PageInfo setPageNumber(int pageNumber);

    /**
     * 一页可以有多少条记录
     */
    int getPageSize();

    /**
     * 设置一页可以有多少条记录
     */
    PageInfo setPageSize(int pageSize);

    /**
     * 整个查询，一共有多少条记录
     */
    int getRecordCount();

    /**
     * 设置整个查询一共有多少条记录
     */
    PageInfo setRecordCount(int recordCount);

    /**
     * 当前页之前，还应该有多少条记录
     */
    int getOffset();

    /**
     * @return 是否是第一页
     */
    boolean isFirst();

    /**
     * @return 是否是最后一页
     */
    boolean isLast();

}
