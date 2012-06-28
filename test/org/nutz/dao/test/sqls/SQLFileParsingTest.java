package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import org.nutz.dao.SqlManager;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.sql.Sql;

public class SQLFileParsingTest {

    private static final String PATH = "org/nutz/dao/test/sqls/sqls.sqls";

    private SqlManager createSqls() {
        return new FileSqlManager(PATH);
    }

    @Test
    public void check_Count_SQL() {
        SqlManager sqls = createSqls();
        assertEquals(10, sqls.count());
        String[] keys = {    ".abc.drop",
                            ".abc.create",
                            ".abc.insert",
                            ".abc.update",
                            "abc.fetch",
                            "abc.query",
                            ".student.drop",
                            ".student.create"};
        for (int i = 0; i < keys.length; i++) {
            assertEquals(keys[i], sqls.keys()[i]);
        }
    }

    @Test
    public void check_Create_SQL() {
        SqlManager sqls = createSqls();
        Sql sql = sqls.create(".abc.create");
        assertTrue(sql.toString().toUpperCase().startsWith("CREATE"));
    }

    @Test
    public void check_Insert_SQL() {
        SqlManager sqls = createSqls();
        Sql sql = sqls.create(".abc.insert");
        assertTrue(sql.toString().toUpperCase().startsWith("INSERT"));
    }

    @Test
    public void check_Update_SQL() {
        SqlManager sqls = createSqls();
        Sql sql = sqls.create(".abc.update");
        assertTrue(sql.toString().toUpperCase().startsWith("UPDATE"));
    }

    @Test
    public void check_Fetch_SQL() {
        SqlManager sqls = createSqls();
        Sql sql = sqls.create("abc.fetch");
        assertTrue(sql.toString().toUpperCase().startsWith("SELECT"));
    }

    @Test
    public void check_Query_SQL() {
        SqlManager sqls = createSqls();
        Sql sql = sqls.create("abc.query");
        assertTrue(sql.toString().toUpperCase().startsWith("SELECT"));
    }

    @Test
    public void check_PersonTestSQLs() {
        SqlManager sqls = new FileSqlManager("org/nutz/dao/test/sqls/sqls.sqls");
        String[] keys = {    ".abc.drop",
                            ".abc.create",
                            ".abc.insert",
                            ".abc.update",
                            "abc.fetch",
                            "abc.query",
                            ".student.drop",
                            ".student.create",
                            ".student2.drop",
                            ".student2.create"};
        for (int i = 0; i < keys.length; i++) {
            assertEquals(keys[i], sqls.keys()[i]);
        }
    }

    @Test
    public void check_parse_comboSqls() {
        SqlManager sqls = new FileSqlManager("org/nutz/dao/test/sqls/sqls.sqls");
        List<Sql> list = sqls.createCombo();
        assertEquals(10, list.size());
    }

    @Test
    public void test_parse_whole_directory() {
        SqlManager sqls = new FileSqlManager("org/nutz/dao/test/sqls/dir");
        assertTrue(sqls.count() > 0);
        Sql sql = sqls.create(".abc.update");
        sql.params().set("name", "ABC");
        sql.params().set("id", 16);
        assertEquals("UPDATE t_abc SET name='ABC' WHERE id=16;", sql.toString());
    }
}
