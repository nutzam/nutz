package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * POJO 查询语句回调对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface PojoCallback {

    /**
     * @param conn
     *            数据库连接
     * @param rs
     *            结果集
     * @param pojo
     *            SQL语句
     * @return 对象会保存在 Pojo 的 result 对象中
     * @throws SQLException
     *             SQL 发生错误
     */
    Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException;

}
