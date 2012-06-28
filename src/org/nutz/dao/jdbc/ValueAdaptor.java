package org.nutz.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 为各个数据库的 JDBC 驱动封装了设值和取值的不同
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ValueAdaptor {

    /**
     * 从结果集里获取一个字段的值
     * 
     * @param rs
     *            结果集
     * @param colName
     *            列名
     * @return 字段值
     * @throws SQLException
     */
    Object get(ResultSet rs, String colName) throws SQLException;

    /**
     * 为缓冲语句设置值
     * <p>
     * 一个值可以被设置到多个占位符中
     * 
     * @param stat
     *            缓冲语句
     * @param obj
     *            值
     * @param index
     *            占位符位置，从 1 开始
     * @throws SQLException
     */
    void set(PreparedStatement stat, Object obj, int index) throws SQLException;

}
