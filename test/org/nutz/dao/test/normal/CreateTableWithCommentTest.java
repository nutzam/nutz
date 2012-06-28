package org.nutz.dao.test.normal;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.lang.Strings;

public class CreateTableWithCommentTest extends DaoCase {

    private static String FIND_TABLE_COMMENT_MYSQL = "select table_comment from information_schema.`tables` where table_name = @tableName";

    private static String FIND_COLUMN_COMMENT_MYSQL = "select column_comment from information_schema.`columns` where table_name = @tableName and column_name = @columnName";

    @Test
    public void createTableInMySQL() throws Exception {
        boolean isMySql = dao.meta().isMySql();
        // 这个仅仅测试MySQL数据库
        if (isMySql) {
            dao.create(TableWithComment.class, true);
            // 表注释
            Sql findTableComment = Sqls.create(FIND_TABLE_COMMENT_MYSQL);
            findTableComment.params().set("tableName", "t_twc");
            findTableComment.setCallback(Sqls.callback.str());
            dao.execute(findTableComment);
            String tableComment = findTableComment.getString();
            Assert.assertTrue(!Strings.isBlank(tableComment) && "测试表".equals(tableComment));
            // 字段注释
            Sql findIdComment = Sqls.create(FIND_COLUMN_COMMENT_MYSQL);
            findIdComment.params().set("tableName", "t_twc").set("columnName", "id");
            findIdComment.setCallback(Sqls.callback.str());
            dao.execute(findIdComment);
            String idComment = findIdComment.getString();
            Assert.assertTrue(!Strings.isBlank(idComment) && "唯一主键".equals(idComment));

            Sql findNameComment = Sqls.create(FIND_COLUMN_COMMENT_MYSQL);
            findNameComment.params().set("tableName", "t_twc").set("columnName", "nm");
            findNameComment.setCallback(Sqls.callback.str());
            dao.execute(findNameComment);
            String nameComment = findNameComment.getString();
            Assert.assertTrue(!Strings.isBlank(nameComment) && "name".equals(nameComment));

            Sql findNumComment = Sqls.create(FIND_COLUMN_COMMENT_MYSQL);
            findNumComment.params().set("tableName", "t_twc").set("columnName", "num");
            findNumComment.setCallback(Sqls.callback.str());
            dao.execute(findNumComment);
            String numComment = findNumComment.getString();
            Assert.assertTrue(Strings.isBlank(numComment));
        }
    }

    @Test
    public void createTableInOracle() throws Exception {
        boolean isOracle = dao.meta().isOracle();
        // 这个仅仅测试Oracle数据库
        if (isOracle) {
            dao.create(TableWithComment.class, true);
            // 表注释
            // 字段注释
        }
    }

    @Test
    public void createTableInPostgersqlAndH2() throws Exception {
        boolean isPostgresql = dao.meta().isPostgresql();
        boolean isH2 = dao.meta().isH2();
        // 这个仅仅测试Postgresql数据库
        if (isPostgresql || isH2) {
            dao.create(TableWithComment.class, true);
            // 表注释
            // 字段注释
        }
    }

    @Test
    public void createTableInDB2() throws Exception {
        boolean isDB2 = dao.meta().isDB2();
        // 这个仅仅测试DB2数据库
        if (isDB2) {
            dao.create(TableWithComment.class, true);
            // 表注释
            // 字段注释
        }
    }

    @Test
    public void createTableInHSQL() throws Exception {
        boolean isHSQL = dao.meta().isHsql();
        // 这个仅仅测试HSQL数据库
        if (isHSQL) {
            dao.create(TableWithComment.class, true);
            // 表注释
            // 字段注释
        }
    }

    @Test
    public void createTableInSqlServer() throws Exception {
        boolean isSqlServer = dao.meta().isSqlServer();
        // 这个仅仅测试SqlServer数据库
        if (isSqlServer) {
            dao.create(TableWithComment.class, true);
            // 表注释
            // 字段注释
        }
    }

}
