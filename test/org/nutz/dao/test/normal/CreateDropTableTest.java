package org.nutz.dao.test.normal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.test.DaoCase;

public class CreateDropTableTest extends DaoCase {

    private static String DESC_TABLE_SQL = "select column_name, is_nullable, column_default, extra from information_schema.`columns` where table_name = @tableName";

    @Test
    public void createTableTimestampFieldDefaultNullInMySQL() throws Exception {
        boolean isMySql = dao.meta().isMySql();
        // 这个仅仅测试MySQL数据库
        if (isMySql) {
            dao.create(TableWithTimestampInMySql.class, true);
            Sql descTable = Sqls.create(DESC_TABLE_SQL);
            descTable.params().set("tableName", "t_ts");
            descTable.setCallback(new SqlCallback() {
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    boolean ts1 = false;
                    boolean ts2 = false;
                    boolean ts3 = false;
                    boolean ts4 = false;
                    while (rs.next()) {
                        String columnName = rs.getString(1);
                        String isNullable = rs.getString(2);
                        String defaultValue = rs.getString(3);
                        String extra = rs.getString(4);
                        if ("ts1".equals(columnName)) {
                            if ((null == defaultValue || "NULL".equalsIgnoreCase(defaultValue))
                                && "YES".equalsIgnoreCase(isNullable)) {
                                ts1 = true;
                            }
                        }
                        if ("ts2".equals(columnName)) {
                            if (null != defaultValue
                                && "0000-00-00 00:00:00".equals(defaultValue)
                                && "YES".equalsIgnoreCase(isNullable)) {
                                ts2 = true;
                            }
                        }
                        if ("ts3".equals(columnName)) {
                            if (null != defaultValue
                                && "0000-00-00 00:00:00".equals(defaultValue)
                                && "NO".equalsIgnoreCase(isNullable)) {
                                ts3 = true;
                            }
                        }
                        if ("ts4".equals(columnName)) {
                            if (null != defaultValue
                                && "CURRENT_TIMESTAMP".equalsIgnoreCase(defaultValue)
                                && "NO".equalsIgnoreCase(isNullable)
                                && "ON UPDATE CURRENT_TIMESTAMP".equalsIgnoreCase(extra)) {
                                ts4 = true;
                            }
                        }
                    }
                    return ts1 && ts2 && ts3 && ts4;
                }
            });
            dao.execute(descTable);
            boolean isTrue = descTable.getObject(Boolean.class);
            Assert.assertTrue(isTrue);
        }
    }

    @Test
    public void createDropTableInOracle() throws Exception {
        boolean isOracle = dao.meta().isOracle();
        if (isOracle) {
            dao.create(CatAutoId.class, true);
            Assert.assertTrue(dao.drop(CatAutoId.class));

            dao.create(CatNotAutoId.class, true);
            Assert.assertTrue(dao.drop(CatNotAutoId.class));
        }
    }
}
