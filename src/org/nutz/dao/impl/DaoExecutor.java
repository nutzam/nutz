package org.nutz.dao.impl;

import java.sql.Connection;

import org.nutz.dao.sql.DaoStatement;

/**
 * Dao 语句执行器
 * <p>
 * 这个类负责具体执行一个 Dao 语句，并负责打印 Log 等 ...
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface DaoExecutor {

    void exec(Connection conn, DaoStatement st);

}
