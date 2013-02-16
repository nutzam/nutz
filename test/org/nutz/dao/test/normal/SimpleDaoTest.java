package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.Nutz;
import org.nutz.castor.Castors;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Abc;
import org.nutz.dao.test.meta.Master;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.PetObj;
import org.nutz.dao.test.meta.SimplePOJO;
import org.nutz.dao.test.meta.issue396.Issue396Master;
import org.nutz.lang.Lang;

public class SimpleDaoTest extends DaoCase {

    public void before() {
        dao.create(Pet.class, true);
    }

    private void insertRecords(int len) {
        for (int i = 0; i < len; i++) {
            Pet pet = Pet.create("pet" + i);
            pet.setNickName("alias_" + i);
            dao.insert(pet);
        }
    }

    @Test
    public void test_simple_fetch_record() {
        Pet pet = Pet.create("abc");
        long now = System.currentTimeMillis();
        pet.setBirthday(Castors.me().castTo(now, Timestamp.class));
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
        assertEquals(0, b.getAge());
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
        assertEquals(2, dao.count(Pet.class, Cnd.wrap("name IN ('pet2','pet3') ORDER BY name ASC")));
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
            dao.insert(Pet.class, Chain.make("name", "record" + i).add("nickName", "Time="+System.currentTimeMillis()));
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
    
    @Test(expected=IllegalArgumentException.class)
    public void test_fetch_null_name() {
        dao.fetch(Pet.class, (String)null);
    }
    
    @Test(expected=IllegalArgumentException.class)
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
}
