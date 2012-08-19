package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.nutz.Nutzs;
import org.nutz.dao.Cnd;
import org.nutz.dao.SqlNotFoundException;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.impl.FileSqlManager;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Country;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Tank;
import org.nutz.trans.Atom;

public class CustomizedSqlsTest extends DaoCase {

    @Test
    public void test_escape_varname_test() {
        Sql sql = Sqls.create("A_$xyz$_B");
        sql.vars().set("xyz", "X");
        assertEquals("A_X_B", sql.toString());
    }

    @Test
    public void test_query_by_limit() {
        // For mysql only
        if (dao.meta().isMySql()) {
            pojos.initPet();
            dao.insert(Pet.create(8));
            assertEquals(8, dao.count(Pet.class));
            Sql sql = Sqls.queryEntity("SELECT * FROM t_pet $condition LIMIT @off,@size ");
            sql.setEntity(dao.getEntity(Pet.class));
            sql.params().set("off", 2).set("size", 2);
            sql.setCondition(Cnd.orderBy().asc("name"));
            dao.execute(sql);
            List<Pet> pets = sql.getList(Pet.class);
            assertEquals(2, pets.size());
            assertEquals("pet_02", pets.get(0).getName());
            assertEquals("pet_03", pets.get(1).getName());

        } else {
            Nutzs.notSupport(dao.meta());
        }
    }

    @Test
    public void test_query_without_entity() {
        pojos.initPet();
        dao.insert(Pet.create(4));
        Sql sql = Sqls.create("SELECT * FROM t_pet $condition");
        sql.setCondition(Cnd.where("name", "like", "pet_%").asc("name"));
        sql.setCallback(new SqlCallback() {
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<Pet> pets = new ArrayList<Pet>(4);
                while (rs.next())
                    pets.add(dao.getObject(Pet.class, rs, null));
                return pets;
            }
        });
        dao.execute(sql);
        List<Pet> pets = sql.getList(Pet.class);
        assertEquals(4, pets.size());
        assertEquals("pet_00", pets.get(0).getName());
        assertEquals("pet_01", pets.get(1).getName());
        assertEquals("pet_02", pets.get(2).getName());
        assertEquals("pet_03", pets.get(3).getName());
    }

    @Test
    public void test_dynamic_insert() {
        pojos.init();
        ((NutDao) dao).setSqlManager(new FileSqlManager("org/nutz/dao/test/sqls/exec.sqls"));
        int platoonId = 23;
        try {
            pojos.initPlatoon(platoonId);
            Sql sql = dao.sqls().create("tank.insert");
            sql.vars().set("id", platoonId);
            sql.params().set("code", "T1").set("weight", 12);
            dao.execute(sql);

            sql = dao.sqls().create("tank.insert");
            sql.vars().set("id", platoonId);
            sql.params().set("code", "T2").set("weight", 13);
            dao.execute(sql);

            sql = dao.sqls().create("tank.insert");
            sql.vars().set("id", platoonId);
            sql.params().set("code", "T3").set("weight", 14);
            dao.execute(sql);

            sql = dao.sqls().create("tank.insert");
            sql.vars().set("id", platoonId);
            sql.params().set("code", "T4").set("weight", 15);
            dao.execute(sql);

            TableName.run(platoonId, new Atom() {
                public void run() {
                    assertEquals(4, dao.count(Tank.class));
                }
            });
        }
        catch (SqlNotFoundException e) {}
        finally {
            pojos.dropPlatoon(platoonId);
        }
    }

    @Test
    public void test_dynamic_query() {
        pojos.init();
        Platoon p = pojos.create4Platoon(Base.make("xyz"), "GG");
        Sql sql = dao.sqls().create("tank.query").setEntity(dao.getEntity(Tank.class));
        sql.vars().set("id", p.getId());
        sql.setCallback(Sqls.callback.entities());
        dao.execute(sql);
        assertEquals(2, sql.getList(Tank.class).size());

        pojos.dropPlatoon(p.getId());
    }

    @Test
    public void test_statice_null_field() {
        pojos.init();
        Sql sql = Sqls.create("INSERT INTO dao_country (name,detail) VALUES(@name,@detail)");
        sql.params().set("name", "ABC").set("detail", "haha");
        dao.execute(sql);
        assertEquals(1, dao.count("dao_country"));

        sql = Sqls.create("UPDATE dao_country SET detail=@detail WHERE name=@name");
        sql.params().set("name", "ABC").set("detail", null);
        dao.execute(sql);
        Country c = dao.fetch(Country.class, "ABC");
        assertNull(c.getDetail());
    }
    
    @Test
    public void test_cnd_pager() {
        pojos.init();
        Sql sql = Sqls.create("select * from t_pet $condition");
        sql.setCondition(Cnd.where("name", "=", "wendal"));
        Pager pager = dao.createPager(1, 20);
        sql.setPager(pager);
        dao.execute(sql);
    }
}