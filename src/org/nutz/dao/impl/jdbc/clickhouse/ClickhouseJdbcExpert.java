package org.nutz.dao.impl.jdbc.clickhouse;

import java.sql.Connection;
import java.sql.SQLException;

import org.nutz.dao.impl.jdbc.mysql.MysqlJdbcExpert;
import org.nutz.dao.jdbc.JdbcExpertConfigFile;

public class ClickhouseJdbcExpert extends MysqlJdbcExpert {

    public ClickhouseJdbcExpert(JdbcExpertConfigFile conf) {
        super(conf);
    }

    @Override
    public void checkDataSource(Connection conn) throws SQLException {}

}
