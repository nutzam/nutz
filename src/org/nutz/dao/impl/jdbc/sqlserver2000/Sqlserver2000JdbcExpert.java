package org.nutz.dao.impl.jdbc.sqlserver2000;

import java.sql.ResultSet;

import org.nutz.dao.impl.jdbc.sqlserver2005.Sqlserver2005JdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;

public class Sqlserver2000JdbcExpert extends Sqlserver2005JdbcExpert {

    public Sqlserver2000JdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    public void formatQuery(Pojo pojo) {
        // 这个指令，可以让 Dao 的语句执行器采用 JDBC 滚动游标的方式来进行分页
        pojo.getContext().setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
    }

    public void formatQuery(Sql sql) {
        // 这个指令，可以让 Dao 的语句执行器采用 JDBC 滚动游标的方式来进行分页
        sql.getContext().setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
    }
    
}
