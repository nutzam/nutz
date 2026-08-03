package org.nutz.dao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.util.Daos;

/**
 * 临时验证用例: 索引迁移(大小写比对/方言化DROP INDEX/getIndexNames过滤系统索引)
 */
public class IndexMigrationVerifyTest {

    @Table("t_verify_idx")
    @TableIndexes(@Index(fields = "age", name = "idx_age"))
    public static class VerifyBean {
        @Id
        private int id;
        @Name
        private String nm;
        private int age;
    }

    static Dao dao;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setDriverClassName(org.nutz.Nutzs.getDriver());
        ds.setJdbcUrl(org.nutz.Nutzs.getUrl());
        ds.setUsername(org.nutz.Nutzs.getUserName());
        ds.setPassword(org.nutz.Nutzs.getPassword());
        dao = new NutDao(ds);
    }

    @Test
    public void test_migration_drop_extra_index() throws Exception {
        dao.create(VerifyBean.class, true);
        // 手工加一个实体定义之外的索引
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                Statement st = conn.createStatement();
                st.execute("CREATE INDEX idx_extra ON t_verify_idx(age)");
                st.close();
            }
        });
        // migration 应删除 idx_extra, 保留 idx_age 及主键/唯一约束
        Daos.migration(dao, VerifyBean.class, true, true, true);
        final Set<String> names = new HashSet<String>();
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                names.addAll(dao.getJdbcExpert().getIndexNames(dao.getEntity(VerifyBean.class), conn));
            }
        });
        boolean hasExtra = false;
        boolean hasIdxAge = false;
        for (String name : names) {
            if ("idx_extra".equalsIgnoreCase(name)) {
                hasExtra = true;
            }
            if ("idx_age".equalsIgnoreCase(name)) {
                hasIdxAge = true;
            }
        }
        assertFalse("多余索引 idx_extra 应被删除", hasExtra);
        assertTrue("实体定义的 idx_age 应保留(验证忽略大小写比对)", hasIdxAge);

        // 再跑一次,验证幂等(旧逻辑会因大小写不匹配而反复删建)
        Daos.migration(dao, VerifyBean.class, true, true, true);
        final Set<String> names2 = new HashSet<String>();
        dao.run(new ConnCallback() {
            public void invoke(Connection conn) throws Exception {
                names2.addAll(dao.getJdbcExpert().getIndexNames(dao.getEntity(VerifyBean.class), conn));
            }
        });
        boolean hasIdxAge2 = false;
        for (String name : names2) {
            if ("idx_age".equalsIgnoreCase(name)) {
                hasIdxAge2 = true;
            }
        }
        assertTrue("二次 migration 后 idx_age 仍应存在", hasIdxAge2);
    }
}
