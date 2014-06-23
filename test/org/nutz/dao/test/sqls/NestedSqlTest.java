package org.nutz.dao.test.sqls;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.Sql;

import static org.junit.Assert.assertTrue;

public class NestedSqlTest {
    static SimpleDataSource ds = new SimpleDataSource();
    static Dao dao = new NutDao();

    static {
        ds.setJdbcUrl("jdbc:h2:mem:");
        ((NutDao) dao).setDataSource(ds);

        dao.create(User.class, true);
        dao.create(Role.class, true);
        dao.create(Permission.class, true);
    }

    @Test
    public void nestedPreparedStatement() {
        Pojo user = dao.queryStatement(User.class, Cnd.where("flags", "=", 3).and("name", "=", "user"));
        Pojo role = dao.queryStatement(Role.class, Cnd.where("flags", "=", 1).and("name", "=", "role"));
        Sql sql = Sqls.create("select * from ($usr) t1 JOIN ($role) t2 on t1.OID = t2.OID where t1.flags=@flags1 and t2.flags=@flags2");
        sql.vars().set("usr", user);
        sql.vars().set("role", role);
        sql.params().set("flags1", 3);
        sql.params().set("flags2", 1);

        System.out.println(sql.toPreparedStatement());

        assertTrue(
            String.format("select * from (%s) t1 JOIN (%s) t2 on t1.OID = t2.OID where t1.flags=? and t2.flags=?"
                , user.toPreparedStatement(), role.toPreparedStatement()
            ).equals(sql.toPreparedStatement())
        );
    }

    @Test
    public void nestedParameter() {
        Pojo user = dao.queryStatement(User.class, Cnd.where("flags", "=", 3).and("name", "=", "user"));
        Pojo role = dao.queryStatement(Role.class, Cnd.where("flags", "=", 1).and("name", "=", "role"));
        Sql sql = Sqls.create("select * from ($usr) t1 JOIN ($role) t2 on t1.OID = t2.OID where t1.flags=@flags1 and t2.flags=@flags2");
        sql.vars().set("usr", user);
        sql.vars().set("role", role);
        sql.params().set("flags1", 3);
        sql.params().set("flags2", 1);

        sql.addBatch();
        sql.params().set("flags1", 33);
        sql.params().set("flags2", 11);

        Object[][] paramMatrix = sql.getParamMatrix();
        StringBuilder sb = new StringBuilder();
        for (Object[] objects : paramMatrix) {
            for (Object o : objects) {
                sb.append(o);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append('\n');
        }

        System.out.println(sb);

        assertTrue("3,user,1,role,3,1\n3,user,1,role,33,11\n".equals(sb.toString()));
    }
}
