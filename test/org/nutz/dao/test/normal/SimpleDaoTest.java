package org.nutz.dao.test.normal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.junit.Test;
import org.nutz.Nutz;
import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoException;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.DaoExecutor;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.impl.sql.NutStatement;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.A;
import org.nutz.dao.test.meta.Abc;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.ColDefineUser;
import org.nutz.dao.test.meta.DynamicTable;
import org.nutz.dao.test.meta.IssuePkVersion;
import org.nutz.dao.test.meta.Master;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.PetObj;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.PojoWithNull;
import org.nutz.dao.test.meta.SimplePOJO;
import org.nutz.dao.test.meta.Soldier;
import org.nutz.dao.test.meta.Tank;
import org.nutz.dao.test.meta.TestMysqlIndex;
import org.nutz.dao.test.meta.UseBlobClob;
import org.nutz.dao.test.meta.issue1074.PojoSql;
import org.nutz.dao.test.meta.issue1163.Issue1163Master;
import org.nutz.dao.test.meta.issue1163.Issue1163Pet;
import org.nutz.dao.test.meta.issue1166.Issue1166;
import org.nutz.dao.test.meta.issue1168.Issue1168;
import org.nutz.dao.test.meta.issue1179.Issue1179;
import org.nutz.dao.test.meta.issue1179.Issue1179Enum;
import org.nutz.dao.test.meta.issue1254.BookTag;
import org.nutz.dao.test.meta.issue1284.Issue1284;
import org.nutz.dao.test.meta.issue1297.DumpData;
import org.nutz.dao.test.meta.issue1297.Issue1297;
import org.nutz.dao.test.meta.issue1302.Issue1302Master;
import org.nutz.dao.test.meta.issue1302.Issue1302UserAction;
import org.nutz.dao.test.meta.issue396.Issue396Master;
import org.nutz.dao.test.meta.issue726.Issue726;
import org.nutz.dao.test.meta.issue901.XPlace;
import org.nutz.dao.test.meta.issue918.Region;
import org.nutz.dao.test.meta.issue928.BeanWithSet;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.dao.util.blob.SimpleClob;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;

public class SimpleDaoTest extends DaoCase {

    public void before() {
        dao.create(Pet.class, true);
    }

    private void insertRecords(int len) {
        for (int i = 0; i < len; i++) {
            Pet pet = Pet.create("pet" + i);
            pet.setNickName("alias_" + i);
            pet.setPrice((float) (R.random(30, 100) / Math.PI));
            dao.insert(pet);
        }
    }

    /**
     * for issue #675 提供一个直接返回对象的方法
     */
    @Test
    public void test_dao_func() {
        insertRecords(10);

        int n = dao.func(Pet.class, "SUM", "price");
        assertTrue(n > 0);

        Object o = dao.func2(Pet.class, "SUM", "price");
        assertTrue((o instanceof Number));
        assertTrue(((Number) o).floatValue() > 0.0f);
    }

    /**
     * for issue #515 写给 mysql 一个特殊的例子
     */
    @Test
    public void test_escape_char() {
        if (dao.meta().isMySql()) {
            dao.insert(Pet.create("A").setNickName("AAA"));
            dao.insert(Pet.create("B").setNickName("B%B"));

            Criteria cri = Cnd.cri();
            cri.where().andLike("alias", "\\%");
            List<Pet> pets = dao.query(Pet.class, cri);
            assertEquals(1, pets.size());
            assertEquals("B", pets.get(0).getName());
        }
    }

    @Test
    public void test_simple_fetch_record() {
        Pet pet = Pet.create("abc");
        long now = System.currentTimeMillis();
        pet.setBirthday(Castors.me().castTo(now / 1000 * 1000, Timestamp.class));
        dao.insert(pet);

        List<Record> pets = dao.query("t_pet", null, null);
        assertEquals(1, pets.size());
        assertEquals("abc", pets.get(0).getString("name"));
        assertEquals(now / 1000, pets.get(0).getTimestamp("birthday").getTime() / 1000);
    }

    @Test
    public void test_delete_list() {
        insertRecords(8);
        List<Pet> list = dao.query(Pet.class, null, null);
        List<Pet> pets = new ArrayList<Pet>(list.size());
        pets.addAll(list);
        assertEquals(8, pets.size());
        pets.addAll(list);
        dao.delete(pets);
        assertEquals(0, dao.count(Pet.class));
    }

    @Test
    public void test_simple_update() {
        dao.fastInsert(Lang.array(Pet.create("A"), Pet.create("B")));
        Pet a = dao.fetch(Pet.class, "A");
        a.setName("C");
        a.setAge(5);

        dao.update(a);

        Pet c = dao.fetch(Pet.class, "C");
        assertEquals("C", c.getName());
        assertEquals(5, c.getAge());

        Pet b = dao.fetch(Pet.class, "B");
        assertEquals("B", b.getName());
    }

    @Test
    public void test_fetch_by_condition_in_special_char() {
        dao.insert(Pet.create("a@b").setNickName("ABC"));
        Pet pet = dao.fetch(Pet.class, Cnd.where("name", "=", "a@b"));
        assertEquals("a@b", pet.getName());
        assertEquals("ABC", pet.getNickName());
    }

    @Test
    public void test_count_with_entity() {
        insertRecords(8);
        int re = dao.count(Pet.class, new Condition() {
            /**
             *
             */
            private static final long serialVersionUID = 6425274813857908874L;

            public String toSql(Entity<?> entity) {
                return entity.getField("nickName").getColumnName() + " IN ('alias_5','alias_6')";
            }
        });
        assertEquals(2, re);
    }

    @Test
    public void test_table_exists() {
        assertTrue(dao.exists(Pet.class));
    }

    @Test
    public void test_count_by_condition() {
        insertRecords(4);
        assertEquals(4, dao.count(Pet.class));
        assertEquals(2,
                     dao.count(Pet.class, Cnd.wrap("name IN ('pet2','pet3') ORDER BY name ASC")));
    }

    @Test
    public void run_2_sqls_with_error() {
        assertEquals(0, dao.count(Pet.class));
        Sql sql1 = Sqls.create("INSERT INTO t_pet (name) VALUES ('A')");
        Sql sql2 = Sqls.create("INSERT INTO t_pet (nocol) VALUES ('B')");
        try {
            dao.execute(sql1, sql2);
            fail();
        }
        catch (DaoException e) {}
        assertEquals(0, dao.count(Pet.class));
    }

    @Test
    public void test_clear_two_records() {
        dao.insert(Pet.create("A"));
        dao.insert(Pet.create("B"));
        assertEquals(2, dao.clear(Pet.class, Cnd.where("id", ">", 0)));
        assertEquals(0, dao.clear(Pet.class, Cnd.where("id", ">", 0)));
    }

    @Test
    public void test_delete_records() {
        dao.insert(Pet.create("A"));
        dao.insert(Pet.create("B"));
        assertEquals(1, dao.delete(Pet.class, "A"));
        assertEquals(1, dao.delete(Pet.class, "B"));
        assertEquals(0, dao.delete(Pet.class, "A"));
    }

    @Test
    public void test_integer_object_column() {
        dao.insert(PetObj.create("X"));
        PetObj pet = dao.fetch(PetObj.class, "X");

        assertEquals("X", pet.getName());
        assertNull(pet.getAge());

        dao.update(pet.setAge(20));
        pet = dao.fetch(PetObj.class, "X");
        assertEquals(20, pet.getAge().intValue());

        dao.update(pet.setAge(null));
        pet = dao.fetch(PetObj.class, "X");
        assertNull(pet.getAge());
    }

    @Test
    public void test_insert_readonly() {
        dao.create(SimplePOJO.class, true);
        SimplePOJO p = new SimplePOJO();
        p.setSex("火星");
        dao.insert(p);
        p.setSex("东方不败");
        dao.update(p);
    }

    @Test
    public void test_order_by() {
        dao.create(Abc.class, true);
        Abc a = new Abc();
        a.setName("ccc");
        dao.insert(a);
        a.setName("abc");
        dao.insert(a);
        dao.query(Abc.class, Cnd.where("id", ">", "-1").asc("name"), null);
    }

    @Test
    public void test_clear() {
        dao.create(Pet.class, true);
        dao.insert(Pet.create("Wendal"));
        dao.insert(Pet.create("Wendal2"));
        dao.insert(Pet.create("Wendal3"));
        dao.insert(Pet.create("Wendal4"));
        dao.insert(Pet.create("Wendal5"));
        assertEquals(5, dao.count(Pet.class));
        assertEquals(5, dao.clear(Pet.class));
    }

    @Test
    public void test_chain_insert() {
        dao.insert(Pet.class, Chain.make("name", "wendal").add("nickName", "asfads"));
    }

    @Test
    public void test_sql_pager() {
        dao.create(Pet.class, true);
        for (int i = 0; i < 100; i++) {
            dao.insert(Pet.class,
                       Chain.make("name", "record" + i).add("nickName",
                                                            "Time=" + System.currentTimeMillis()));
        }
        Pager pager = dao.createPager(5, 5);
        pager.setRecordCount(dao.count(Pet.class));
        Sql sql = Sqls.queryEntity("select * from t_pet");
        sql.setEntity(dao.getEntity(Pet.class));
        sql.setPager(pager);
        dao.execute(sql);

        List<Pet> pets = sql.getList(Pet.class);
        assertNotNull(pets);
        assertEquals(5, pets.size());
        assertEquals("record20", pets.get(0).getName());
        assertEquals("record21", pets.get(1).getName());
        assertEquals("record22", pets.get(2).getName());
        assertEquals("record23", pets.get(3).getName());
        assertEquals("record24", pets.get(4).getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_fetch_null_name() {
        dao.fetch(Pet.class, (String) null);
    }

    @Test(expected = Exception.class)
    public void test_create_error_class() {
        dao.create(Nutz.class, true);
    }

    // issue 395 删除一个不存在的管理对象
    @Test
    public void test_delete_null_many() {
        dao.create(Master.class, true);
        Master master = new Master();
        master.setName("ACB");
        dao.insert(master);
        master = dao.fetch(Master.class);
        dao.fetchLinks(master, null);
        dao.deleteWith(master, null);
    }

    // issue 396
    @Test
    public void test_insert_with() {
        if (!dao.meta().isOracle())
            return;
        dao.create(Issue396Master.class, true);
    }

    @Test
    public void test_insert_special_chain() {
        if (dao.meta().isMySql())
            dao.insert(Pet.class, Chain.makeSpecial("birthday", "now()").add("name", "wendal"));
    }

    @Test
    public void test_issue_726() {
        dao.create(Issue726.class, true);
        boolean ai = dao.getEntity(Issue726.class).getColumn("id").isAutoIncreasement();
        if (!dao.meta().isPostgresql())
            assertTrue(ai);
        else
            assertFalse(ai);
    }

    @Test
    public void test_daoex_update() throws Throwable {
        final List<A> list = new ArrayList<A>();
        for (int i = 0; i < 1; i++) {
            A a = new A();
            a.setUid(System.currentTimeMillis());
            Lang.quiteSleep(10);
            // a.setName("zzzz" + System.currentTimeMillis());
            list.add(a);
        }
        TableName.run("1", new Runnable() {
            public void run() {
                dao.create(A.class, true);
                dao.update(list);
            }
        });
        System.out.println("\n\n\n\n\n\n\n\n");
        Daos.ext(dao, FieldFilter.create(A.class, null, "^(name)$", true), 1).update(list);

    }

    @Test
    public void test_bean_uuid() {
        Sql sql = Sqls.queryRecord("select * from t_pet");
        sql.setPager(dao.createPager(1, 10));
        dao.execute(sql);
    }

    @Test
    public void test_fetchLinks() {
        dao.create(Master.class, true);
        Master master = new Master();
        master.setName("wendal");
        Pet pet = Pet.create("asdfs");
        Pet pet2 = Pet.create("zzzz");
        List<Pet> pets = new ArrayList<Pet>();
        pets.add(pet);
        pets.add(pet2);
        master.setPets(pets);
        dao.insertWith(master, null);
        List<Master> list = dao.query(Master.class, null);
        dao.fetchLinks(list, null, Cnd.where("1", "=", 1));
    }

    @Test
    public void test_insert_with_id() {
        if (dao.meta().isSqlServer())
            return;
        dao.clear(Pet.class);
        Pet pet = Pet.create("zzz");
        pet.setId(9090); // 主动设置id
        Dao dao = Daos.ext(this.dao,
                           FieldFilter.create(Pet.class,
                                              FieldMatcher.make(null, null, true)
                                                          .setIgnoreId(false)));
        dao.fastInsert(pet);
        pet = dao.fetch(Pet.class); // 只有一条记录
        assertEquals(9090, pet.getId());

        // / 然后用1.b.53的新方法测试一下
        if (dao.meta().isPostgresql()) {
            System.out.println("因为Pet的@Id配置了@Next,导致插入后再执行里面的sql会报错");
            // 还没想到怎么解决, FieldMatcher存在的时候忽略@Next?
            return;
        }

        dao.clear(Pet.class);
        pet = Pet.create("zzz");
        pet.setId(9090); // 主动设置id
        dao.insert(pet, FieldFilter.create(Pet.class, FieldMatcher.create(false)));
        pet = dao.fetch(Pet.class); // 只有一条记录
        assertEquals(9090, pet.getId());
    }

    @Test
    public void test_use_blob_clob() {
        dao.create(UseBlobClob.class, true);
        UseBlobClob use = new UseBlobClob();
        use.setName("wendal");
        use.setX(new SimpleBlob(Files.findFile("nutz-test.properties")));
        use.setY(new SimpleClob(Files.findFile("nutz-test.properties")));
        use = dao.insert(use);

        use.setX(new SimpleBlob(Files.findFile("log4j.properties")));
        use.setY(new SimpleClob(Files.findFile("log4j.properties")));
        dao.update(use);
    }

    @Test
    public void test_migration() {
        dao.execute(Sqls.create("drop table t_pet"));
        Entity<Pet> en = dao.getEntity(Pet.class);
        NutDao dao = (NutDao) this.dao;
        JdbcExpert expert = dao.getJdbcExpert();
        MappingField mf = en.getField("age");
        String str = "create table t_pet ("
                     + mf.getColumnName()
                     + " "
                     + expert.evalFieldType(mf)
                     + ","
                     + mf.getColumnName()
                     + "_2"
                     + " "
                     + expert.evalFieldType(mf)
                     + ")";
        dao.execute(Sqls.create(str));

        Daos.migration(dao, Pet.class, !dao.meta().isSQLite(), !dao.meta().isSQLite());

    }

    @Test
    public void test_dynamic_migration() {
        for (int i = 0; i < 3; i++) {
            Daos.ext(dao, i).create(DynamicTable.class, true);
        }

        for (int i = 0; i < 3; i++) {
            dao.execute(Sqls.createf("ALTER table dynamic_table%s DROP column createTime", i));
            Daos.migration(dao, DynamicTable.class, true, true, i);
        }

    }

    @Test
    public void test_varchar_BigDecimal() {
        dao.create(XPlace.class, true);
        XPlace place = new XPlace();
        place.setLat(new BigDecimal("12.3222"));
        place.setLng(new BigDecimal("29.02333"));
        dao.insert(place);

        place = dao.fetch(XPlace.class);
        assertEquals("12.3222", place.getLat().toString());
        assertEquals("29.02333", place.getLng().toString());

        System.out.println(Json.toJson(place));
    }

    @Test
    public void test_xxx() {
        Sql task_sql = Sqls.create("SELECT * FROM act_ru_task WHERE CATEGORY_=@category AND ( ASSIGNEE_=@userId  $assignee ) ORDER BY create_time_ desc");
        task_sql.params().set("category", 1);
        task_sql.params().set("userId", 2);
        task_sql.vars().set("assignee", "and name != 'hi'");

        System.out.println(task_sql.toPreparedStatement());
        System.out.println(task_sql.forPrint());
        System.out.println(">>" + task_sql);
    }

    @Test
    public void test_issue_918() {
        dao.create(Region.class, true);
    }

    @Test
    public void test_issue_928() {
        dao.create(BeanWithSet.class, true);
        BeanWithSet s = new BeanWithSet();
        Set<Long> uids = new HashSet<Long>();
        Long UID = 1234455656L;
        uids.add(UID);
        s.setUids(uids);

        Set<String> names = new HashSet<String>();
        names.add("wendal");
        s.setNames(names);
        System.out.println(names);

        dao.insert(s);

        BeanWithSet out = dao.fetch(BeanWithSet.class);
        assertEquals(Long.class, out.getUids().iterator().next().getClass());
        assertEquals(UID, out.getUids().iterator().next());

    }

    @Test
    public void test_query_2() {
        dao.insert(Pet.create(100));
        List<Pet> list = dao.query(Pet.class, null);
        for (Pet pet : list) {
            assertNotNull(pet.getName());
        }
    }

    @Test
    public void test_get_sql() throws Throwable {
        NutDao dao = new NutDao(ioc.get(javax.sql.DataSource.class));
        final List<String> sqls = new ArrayList<String>();
        final Method m = NutStatement.class.getDeclaredMethod("toStatement",
                                                              Object[][].class,
                                                              String.class);
        m.setAccessible(true);
        dao.setExecutor(new DaoExecutor() {
            public void exec(Connection conn, DaoStatement st) {
                String psql = st.toPreparedStatement();
                sqls.add(psql);
                // 如果需要带数据的, 因为nutz并不生成和执行带数据的sql,所以需要通过
                // st.toPreparedStatement() 与 参数矩阵 st.getParamMatrix() 综合生成
                // 这里调用非公开api中的toStatement
                // 事实上根据参数矩阵(getParamMatrix)和Sql语句(toPreparedStatement)就能逐一替换生成
                try {
                    String ssql = (String) m.invoke(st, st.getParamMatrix(), psql);
                    sqls.add(ssql);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dao.insert(Pet.create("hi"));
        for (String sql : sqls) {
            System.out.println(sql);
        }
    }

    /**
     * 插入时忽略空值
     */
    @Test
    public void test_daos_ext_insert_without_null() {
        dao.create(PojoWithNull.class, true);
        final PojoWithNull p = new PojoWithNull();
        p.setName("hi");
        p.setNickname("wendal");
        // dao.insert(p.getClass(), Chain.from(p, FieldMatcher.make(null, null,
        // true)));
        // Daos.ext(dao, FieldFilter.create(p.getClass(), true)).insert(p);

        dao.insert(p, FieldFilter.create(p.getClass(), true));
    }

    // @Test
    // public void test_map_blob() throws FileNotFoundException {
    // if (dao.exists("t_test_map_blob")) {
    // dao.drop("t_test_map_blob");
    // Lang.quiteSleep(1000);
    // }
    // dao.execute(Sqls.create("create table t_test_map_blob(id
    // VARCHAR(60),filecontent blob)"));
    //
    // NutMap map = new NutMap().setv(".table", "t_test_map_blob");
    // map.put("id", R.UU32());
    // map.put("filecontent", new
    // FileInputStream("W:\\usb3.0_intel_1.0.10.255_w7.zip"));
    //
    // dao.insert(map);
    //
    // Record re = dao.fetch("t_test_map_blob", Cnd.NEW());
    // assertNotNull(re);
    // System.out.println(re.get("filecontent").getClass());
    // System.out.println(new String((byte[])re.get("filecontent")));
    //
    //// assertEquals("你好", new String((byte[])re.get("filecontent")));
    // }

    // @Test
    public void test_fastinsert_speed() {
        SimpleDataSource ds = new SimpleDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost/nutztest");
        ds.setUsername("root");
        ds.setPassword("root");
        dao = new NutDao(ds);
        // 删表重建
        dao.create(Pet.class, true);
        Lang.sleep(1000);

        Stopwatch sw = Stopwatch.begin();
        // 生成10*2000个对象
        List<List<Pet>> list = new ArrayList<List<Pet>>();
        for (int i = 0; i < 10; i++) {
            List<Pet> pets = new ArrayList<Pet>();
            for (int j = 0; j < 2000; j++) {
                Pet pet = Pet.create(R.UU32());
                pets.add(pet);
            }
            list.add(pets);
        }
        sw.stop();

        System.out.println("生成对象的耗时: " + sw);

        for (final List<Pet> tmp : list) {
            sw = Stopwatch.begin();
            Trans.exec(new Atom() {
                public void run() {
                    dao.fastInsert(tmp);
                }
            });

            sw.stop();

            System.out.println("fastInsert插入2000个对象的耗时" + sw);
        }

        dao.create(Pet.class, false);
        for (int i = 0; i < 10; i++) {
            try {
                final int t = i;
                Connection conn = ds.getConnection();
                conn.setAutoCommit(false);
                sw = Stopwatch.begin();
                System.out.println(System.currentTimeMillis());
                PreparedStatement ps = conn.prepareStatement("INSERT INTO t_pet(name,alias) VALUES(?,?)");
                System.out.println(System.currentTimeMillis());
                for (int j = 0; j < 2000; j++) {
                    ps.setString(1, "pet_" + t + "_" + j);
                    ps.setString(2, "");
                    // ps.setInt(3, 30);
                    // ps.setInt(4, 0);
                    // ps.setDate(5, null);
                    // ps.setFloat(6, 0);
                    ps.addBatch();
                }
                System.out.println(System.currentTimeMillis());
                ps.executeBatch();
                conn.commit();
                sw.stop();
                System.out.println(sw);
                ps.close();
                conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test_insert_ignore_null() {
        Pet pet = Pet.create(R.UU32());
        dao.insert(pet, true, true, true);
        assertTrue(pet.getId() > 0);
        dao.insert(Pet.create(1000), true, true, true);
    }

    @Test
    @Deprecated
    public void test_daos_queryCount() {
        String str = "select * from t_pet";
        Daos.queryCount(dao, str);
    }

    @Test
    public void test_cnd_andEX_orEX() {
        String emtryStr = "";
        Object[] ids = new Object[0];
        List<String> names = new ArrayList<String>();

        Cnd cnd = Cnd.NEW();

        cnd.andEX("name", "=", emtryStr); // 空字符串,所以该条件不生效
        cnd.andEX("name", "=", "wendal");
        cnd.orEX("id", "in", ids);
        cnd.orEX("id", ">", 1);
        cnd.andEX("names", "in", names);

        assertEquals("WHERE name='wendal' OR id>1", cnd.toString().trim());
    }

    @Test
    public void test_insert_chain_with_null() {
        dao.create(Pet.class, true);
        dao.insert("t_pet", Chain.make("name", "wendal").add("alias", null));
    }

    @Test
    public void test_cnd_emtry_in() {
        assertEquals(" WHERE  1 != 1 ", Cnd.where("name", "in", Collections.EMPTY_LIST).toString());
        assertEquals(" WHERE  1 != 1 ", Cnd.where("name", "in", new String[0]).toString());
        assertEquals(" WHERE  1 != 1 ", Cnd.where("id", "in", new int[]{}).toString());
    }

    @Test
    public void new_nutdao_inside_trans() {
        // 这纯粹是重现bug的代码,不要学
        final DataSource ds = ioc.get(DataSource.class);
        Trans.exec(new Atom() {
            public void run() {
                new NutDao(ds);
            }
        });
    }

    @Test
    public void test_pojo_sql_params() {
        dao.create(PojoSql.class, false);
        PojoSql pojo = new PojoSql();
        pojo.setName(R.UU32());
        dao.insert(pojo);

        assertEquals(pojo.getName(), pojo.getNickname());
    }

    @Test
    public void test_record_treemap() {
        TreeMap<Record, String> map = new TreeMap<Record, String>();
        map.put(new Record(), "abc");
        map.put(new Record(), "abc");

    }

    @Test
    public void test_count_groupby() {
        if (!dao.meta().isMySql())
            return;
        // MySQL/PgSQL/SqlServer 与 Oracle/H2的结果会不一样,奇葩啊
        for (int i = 0; i < 10; i++) {
            Pet pet = Pet.create(R.UU32());
            pet.setAge(20 + i / 4);
            dao.insert(pet);
        }
        Cnd cnd = Cnd.where("age", ">", 20);
        cnd.groupBy("age");
        assertEquals(10, dao.count(Pet.class, null));
        assertEquals(4, dao.count(Pet.class, cnd));
    }

    @Test
    public void test_issue_1163() {
        dao.create(Issue1163Master.class, true);
        dao.create(Issue1163Pet.class, true);

        ArrayList<Issue1163Pet> pets = new ArrayList<Issue1163Pet>();
        for (int i = 0; i < 3; i++) {
            Issue1163Pet pet = new Issue1163Pet();
            pet.setName("i" + i);
            pets.add(pet);
        }
        Issue1163Pet gpet = new Issue1163Pet();
        gpet.setName("zozoh");

        Issue1163Master master = new Issue1163Master();
        master.setName("wendal");
        master.setPets(pets);
        master.setGpet(gpet);

        dao.insertWith(master, null);
    }

    @Test(expected = DaoException.class)
    public void test_issue_1166() {
        dao.create(Issue1166.class, true);
        Daos.migration(dao, Issue1166.class, true, true, true);
        Issue1166 is = new Issue1166();
        is.setName("wendal");

        dao.insert(is);
        dao.insert(is);
    }

    @Test
    public void test_issue_1168() {
        dao.create(Issue1168.class, true);
        Issue1168 right = new Issue1168();
        dao.insert(right);
    }

    @Test
    public void test_coldefine_len() {
        dao.create(ColDefineUser.class, true);
        ColDefineUser user = new ColDefineUser();
        user.setName("admin");
        user.setSalt(R.UU32());
        user.setPassword(Lang.sha1("abc" + user.getSalt()));
        dao.insert(user);
        
        NutMap map = new NutMap(".table", "t_test_user");
        map.put("+*id", 0);
        map.put("name", "wendal");
        dao.insert(map);
        assertNotNull(map.get("id"));
        
        map = new NutMap(".table", "t_test_user");
        map.put("*+id", 0);
        map.put("name", "wendal2");
        dao.insert(map);
        assertNotNull(map.get("id"));
    }

    @Test
    public void test_issue_1179() {
        dao.create(Issue1179.class, true);
        Issue1179 pojo = new Issue1179();
        pojo.setName("https://nutz.cn/?" + R.UU32());
        pojo.setSt(Issue1179Enum.GHI);
        dao.insert(pojo);
        dao.fetch(Issue1179.class);
    }

    @Test
    public void test_issue_1235() {
        dao.create(Pet.class, false);
        dao.insert(Pet.create(R.UU32()));
        List<Record> list = dao.query("t_pet", null, null, "id,name");
        assertNotNull(list);
        assertTrue(list.size() > 0);
        assertEquals(2, list.get(0).size());
    }

    @Test
    public void test_fetch_by_join() {
        dao.create(Platoon.class, true);
        dao.create(Soldier.class, true);
        dao.create(Base.class, true);
        dao.create(Tank.class, true);
        Platoon platoon = new Platoon();
        Platoon platoon1 = new Platoon();
        Platoon platoon2 = new Platoon();
        platoon.setName("wendal");
        platoon1.setName("xiaomo");
        platoon2.setName("test");

        Soldier soldier = new Soldier();
        Soldier soldier1 = new Soldier();
        Soldier soldier2 = new Soldier();
        soldier.setName("stone");
        soldier1.setName("stone_sz");
        soldier2.setName("stone_sc");

        Base base = new Base();
        Base base1 = new Base();
        Base base2 = new Base();
        base.setName("china");
        base1.setName("china_sz");
        base2.setName("china_sc");

        platoon.setBase(base);
        platoon1.setBase(base1);
        platoon2.setBase(base2);
        platoon.setLeader(soldier);
        platoon1.setLeader(soldier1);
        platoon2.setLeader(soldier2);
        dao.insertWith(platoon, null);
        dao.insertWith(platoon1, null);
        dao.insertWith(platoon2, null);

        // =======================================
        // 用条件查
        platoon = dao.fetchByJoin(Platoon.class,
                                  null,
                                  Cnd.where("dao_platoon.name", "=", "wendal"));

        assertNotNull(platoon);
        assertEquals("wendal", platoon.getName());

        assertNotNull(platoon.getLeader());
        assertEquals("stone", platoon.getLeader().getName());

        assertNotNull(platoon.getBase());
        assertEquals("china", platoon.getBase().getName());

        // =======================================
        // 用@Name
        platoon = dao.fetchByJoin(Platoon.class, null, "wendal");

        assertNotNull(platoon);
        assertEquals("wendal", platoon.getName());

        assertNotNull(platoon.getLeader());
        assertEquals("stone", platoon.getLeader().getName());

        assertNotNull(platoon.getBase());
        assertEquals("china", platoon.getBase().getName());

        // =======================================
        // 用@Id
        platoon = dao.fetchByJoin(Platoon.class, null, platoon.getId());

        assertNotNull(platoon);
        assertEquals("wendal", platoon.getName());

        assertNotNull(platoon.getLeader());
        assertEquals("stone", platoon.getLeader().getName());

        assertNotNull(platoon.getBase());
        assertEquals("china", platoon.getBase().getName());

        // =======================================
        // @One分页测试，总共3个，分页的为2个
        assertEquals(3,dao.queryByJoin(Platoon.class, null, null).size());
        assertEquals(2,dao.queryByJoin(Platoon.class, null, null,new Pager(1, 2)).size());
    }

    @Test
    public void test_migration_issue1254() {
        if (!dao.meta().isMySql())
            return;
        try {
            dao.execute(Sqls.create("drop table t_issue1254_book"));
            dao.execute(Sqls.create("drop table t_issue1254_book_tag"));
            dao.execute(Sqls.create("drop table t_issue1254_tag"));
        }
        catch (Exception e) {}

        Daos.FORCE_WRAP_COLUMN_NAME = true;
        try {
            Daos.createTablesInPackage(dao, BookTag.class, false);
            Daos.migration(dao,
                           BookTag.class.getPackage().getName(),
                           !dao.meta().isSQLite(),
                           !dao.meta().isSQLite());

            Entity<BookTag> en = dao.getEntity(BookTag.class);
            MappingField mf = en.getField("id");
            assertTrue(mf.isId());
            assertTrue(mf.isPk());
        } finally {
            Daos.FORCE_WRAP_COLUMN_NAME = false;
        }
        
    }
    
    @Test
    public void test_fast_insert_maps() {
        List<NutMap> list = new ArrayList<NutMap>();
        for (int i = 0; i < 100; i++) {
            NutMap pet = new NutMap();
            pet.setv("name", "wendal" + i).setv("age", i);
            list.add(pet);
        }
        list.get(0).setv(".table", "t_pet");
        
        dao.fastInsert(list);
    }
    
    @Test
    public void test_issue_1284() {
        dao.create(Issue1284.class, true);
        Entity<Issue1284> en = dao.getEntity(Issue1284.class);
        assertFalse(en.getIdField().isAutoIncreasement());
        Issue1284 bean = new Issue1284();
        bean.setAge(20);
        dao.insert(bean);
    }
    
    @Test
    public void test_issue_insert_or_update() {
        try {
            dao.create(Issue1297.class, false);
            Issue1297 pojo = new Issue1297();
            pojo.setCt(new Timestamp(System.currentTimeMillis()));
            pojo.setKeySn("ABC");
            pojo.setUserid(123);
            dao.insertOrUpdate(pojo);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
        
        try {
            dao.create(DumpData.class, true);
            DumpData dump = new DumpData();
            dump.setId(R.UU32());
            dump.setTitle("ABC");
            dao.insertOrUpdate(dump);
            dump.setTitle("DDD");
            dao.insertOrUpdate(dump);
            assertEquals(1, dao.count(DumpData.class));
            assertEquals("DDD", dao.fetch(DumpData.class).getTitle());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    @Test
    public void test_issue_1302() {
            dao.create(Issue1302Master.class, false);
            Issue1302Master pojo = new Issue1302Master();
            pojo.setName(R.UU32());
            pojo.setAct(Issue1302UserAction.VIEW);
            dao.insert(pojo);
            System.out.println(DB.SQLSERVER.ordinal());
            pojo = dao.fetch(Issue1302Master.class, pojo.getName());
            assertEquals(Issue1302UserAction.VIEW, pojo.getAct());
    }
    

    @Test
    public void test_truncate() {
            // 建好表,插入10条记录
            dao.create(Pet.class, false);
            dao.insert(Pet.create(10));
            assertTrue(dao.count(Pet.class) > 0);
            
            // 干掉
            dao.truncate(Pet.class);
            assertTrue(dao.count(Pet.class) == 0);
            
            // 再插入10条记录
            dao.insert(Pet.create(10));
            assertTrue(dao.count(Pet.class) > 0);
            
            //再干掉
            dao.truncate(dao.getEntity(Pet.class).getTableName());
            assertTrue(dao.count(Pet.class) == 0);
    }
    
    @Test
    public void test_issue1342() {
        if (!dao.meta().isMySql())
            return;
        if (dao.exists("t_issue_1342"))
            dao.drop("t_issue_1342");
        dao.execute(Sqls.create("create table t_issue_1342(id INT AUTO_INCREMENT,order_day DATETIME NOT NULL, PRIMARY KEY(id, order_day)) "
                + "PARTITION BY RANGE(YEAR(order_day)) ("
                + "PARTITION p_2017 VALUES LESS THAN (2017),"
                + "PARTITION p_catchall VALUES LESS THAN MAXVALUE)"));
        dao.query("t_issue_1342", new SimpleCriteria("partition(p_2017)"));
    }
    
    @Test
    public void test_pk_version() {
        dao.create(IssuePkVersion.class, true);
        for (int i = 0; i < 10; i++) {
            IssuePkVersion v = new IssuePkVersion();
            v.setName("abc_" + i);
            v.setAge(i);
            v.setPrice(i*100);
            v.setVersion(0);
            dao.insert(v);
        }
        assertEquals(10, dao.count(IssuePkVersion.class));
        IssuePkVersion ve = dao.fetchx(IssuePkVersion.class, "abc_1", 1);
        assertNotNull(ve);
        ve.setPrice(99);
        dao.updateWithVersion(ve);
        ve = dao.fetchx(IssuePkVersion.class, "abc_1", 1);
        assertEquals(99, ve.getPrice());
    }
    
    @Test
    public void test_mysql_migration() {
        if (!dao.meta().isMySql())
            return;
        dao.create(TestMysqlIndex.class, true);
        
        System.out.println("==================================");
        Daos.migration(dao, TestMysqlIndex.class, true, false, true);
        System.out.println("==================================");
        Daos.migration(dao, TestMysqlIndex.class, true, false, true);
        Daos.migration(dao, TestMysqlIndex.class, true, false, true);
        Daos.migration(dao, TestMysqlIndex.class, true, false, true);
        System.out.println("==================================");
    }
    
    @Test
    public void test_insert_chain_with_adaptor() {
        dao.create(Pet.class, true);
        dao.insert("t_pet", Chain.make("name", "wendal").adaptor(Jdbcs.Adaptor.asString));
    }
}
