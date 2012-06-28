package org.nutz.dao.pager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.sql.SqlContext;

/**
 * 遍历 RersultSet
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class ResultSetLooping {

    protected List<Object> list;

    private int index;

    public ResultSetLooping() {
        list = new LinkedList<Object>();
        index = -1;
    }

    public void doLoop(ResultSet rs, SqlContext context) throws SQLException {
        Pager pager = context.getPager();
        if (null == rs)
            return;
        /**
         * 如果没有设置 Pager 或者 rs 的类型是 ResultSet.TYPE_FORWARD_ONLY，那么<br>
         * 无法利用 游标的滚动 来计算结果集合大小。这比较高效，但是如果使用者希望得到页数量，<br>
         * 需要为 Pager 另行计算 总体的结果集大小。
         * <p>
         * 一般的，为特殊数据建立的 Pager，生成的 ResultSet 类型应该是 TYPE_FORWARD_ONLY
         */
        if (null == pager
            || ResultSet.TYPE_FORWARD_ONLY == rs.getType()
            || pager.getPageNumber() <= 0) {
            // 根据 Pager 设定 Fetch Size
            // by wendal: 设置与否,影响不大的,而且旧版本的Oracle会出问题,故,注释掉了
            //if (null != pager && pager.getPageSize() > 0)
            //    rs.setFetchSize(pager.getPageSize());

            // 循环调用
            while (rs.next()) {
                createObject(++index, rs, context, -1);
            }
        }
        /**
         * 如果进行到了这个分支，则表示，整个查询的 Pager 是不区分数据库类型的。 <br>
         * 并且 ResultSet 的游标是可以来回滚动的。
         * <p>
         * 所以我就会利用游标的滚动，为你计算整个结果集的大小。比较低效，在很小<br>
         * 数据量的时候 还是比较有用的
         */
        else if (rs.last()) {
            // 设置结果集合的 FetchSize
            if (pager.getPageSize() <= 0)
                rs.setFetchSize(Pager.DEFAULT_PAGE_SIZE);
            else if (pager.getPageSize() > Pager.MAX_FETCH_SIZE)
                rs.setFetchSize(Pager.MAX_FETCH_SIZE);
            else
                rs.setFetchSize(pager.getPageSize());

            // 开始循环
            int rowCount = rs.getRow();
            LoopScope ls = LoopScope.eval(pager, rowCount);
            if (rs.absolute(ls.start + 1))
                for (int i = ls.start; i < ls.max; i++) {
                    createObject(++index, rs, context, rowCount);
                    if (!rs.next())
                        break;
                }
        }
    }

    /**
     * @return 当前获取的 List
     */
    public List<Object> getList() {
        return list;
    }

    /**
     * 获得最后一次回调被调用时的下标。 index 的值初始为 -1，每次调用回调前都会自增
     * 
     * @return 当前循环的下标，下标由 0 开始
     */
    public int getIndex() {
        return index;
    }

    /**
     * 子类需要实现的函数
     * 
     * @param index
     *            当前下标
     * @param rs
     *            结果集
     * @param context
     *            Sql 上下文
     * @param rowCount
     *            总记录数，如果是原生分页语句，则会为 -1
     * @return 是否成功的创建了对象
     */
    protected abstract boolean createObject(int index,
                                            ResultSet rs,
                                            SqlContext context,
                                            int rowCount);

}
