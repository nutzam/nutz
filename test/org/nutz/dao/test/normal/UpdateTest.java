package org.nutz.dao.test.normal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.BeanWithDefault;
import org.nutz.dao.test.meta.Fighter;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.issue1244.VersionTestPojo;
import org.nutz.dao.test.meta.other.UpdateClobBlobBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;

public class UpdateTest extends DaoCase {

    /**
     * For issue #557
     */
    @Test
    public void test_update_ignore_null() {
        dao.create(Pet.class, true);
        final Pet pet = Pet.create("XiaoBai").setAge(20);
        dao.insert(pet);

        FieldFilter.create(Pet.class, true).run(new Atom() {
            public void run() {
                Pet p1 = new Pet().setAge(12).setId(pet.getId());
                dao.update(p1);
            }
        });

        Pet p2 = dao.fetch(Pet.class, pet.getId());
        assertEquals("XiaoBai", p2.getName());
    }

    /**
     * For issue #84
     */
    @Test
    public void test_updateIgnoreNull_width_default() {
        dao.create(BeanWithDefault.class, true);
        BeanWithDefault bean = new BeanWithDefault();
        bean.setName("abc");
        dao.insert(bean);

        BeanWithDefault b2 = dao.fetch(BeanWithDefault.class);
        assertEquals("--", b2.getAlias());

        b2.setAlias("AAA");
        dao.update(b2);

        b2 = dao.fetch(BeanWithDefault.class, "abc");
        assertEquals("AAA", b2.getAlias());

        b2.setAlias(null);
        dao.updateIgnoreNull(b2);

        b2 = dao.fetch(BeanWithDefault.class, "abc");
        assertEquals("AAA", b2.getAlias());
    }

    @Test
    public void test_update_chain_and_cnd_by_in() {
        dao.create(Pet.class, true);
        Pet pet = Pet.create("xb");
        pet.setNickName("XB");
        dao.insert(pet);

        dao.update(Pet.class,
                   Chain.make("name", "xiaobai"),
                   Cnd.where("nickName", "in", Lang.array("XB")));
        pet = dao.fetch(Pet.class, "xiaobai");
        assertEquals("XB", pet.getNickName());
    }

    @Test
    public void test_update_chain_and_cnd() {
        dao.create(Pet.class, true);
        Pet pet = Pet.create("xb");
        pet.setNickName("XB");
        dao.insert(pet);

        dao.update(Pet.class,
                   Chain.make("name", "xiaobai"),
                   Cnd.where("nickName", "=", "XB"));
        pet = dao.fetch(Pet.class, "xiaobai");
        assertEquals("XB", pet.getNickName());
    }

    @Test
    public void batch_update_all() {
        pojos.initData();
        dao.update(Fighter.class,
                   Chain.make("type", Fighter.TYPE.SU_35.name()),
                   null);
        assertEquals(13,
                     dao.count(Fighter.class,
                               Cnd.where("type", "=", Fighter.TYPE.SU_35.name())));
    }

    @Test
    public void batch_update_partly() {
        pojos.initData();
        int re = dao.update(Fighter.class,
                            Chain.make("type", "F15"),
                            Cnd.where("type", "=", "SU_35"));
        assertEquals(1, re);
        int maxId = dao.getMaxId(Fighter.class);
        re = dao.update(Fighter.class,
                        Chain.make("type", "UFO"),
                        Cnd.where("id", ">", maxId - 5));
        assertEquals(5, re);
        assertEquals(re,
                     dao.count(Fighter.class, Cnd.where("type", "=", "UFO")));
    }

    @Test
    public void batch_update_relation() {
        pojos.initData();
        dao.updateRelation(Fighter.class,
                           "base",
                           Chain.make("bname", "blue"),
                           Cnd.where("bname", "=", "red"));
        assertEquals(13,
                     dao.count("dao_m_base_fighter",
                               Cnd.where("bname", "=", "blue")));
    }

    @Test
    public void fetch_by_name_ignorecase() {
        pojos.initData();
        Platoon p = dao.fetch(Platoon.class, "sF");
        assertEquals("SF", p.getName());
    }

    @Test
    public void test_update_obj_with_readonly_field() {
        dao.create(Plant.class, true);
        Plant p = new Plant();
        p.setNumber(100);
        p.setColor("red");
        p.setName("Rose");
        dao.insert(p);

        p = dao.fetch(Plant.class, "Rose");
        assertNull(p.getColor());
        assertEquals(100, p.getNumber());

        p.setColor("black");
        p.setNumber(88);
        dao.update(p);

        p = dao.fetch(Plant.class, "Rose");
        assertNull(p.getColor());
        assertEquals(88, p.getNumber());
    }

    @Test
    public void update_with_null_links() {
        pojos.initData();
        Platoon p = dao.fetch(Platoon.class, "sF");
        p.setLeaderName("xyz");
        dao.updateWith(p, null);
        p = dao.fetch(Platoon.class, "sF");
        assertEquals("xyz", p.getLeaderName());
    }

    @Test
    public void test_updateIgnoreNull() {
        pojos.initData();
        Platoon p = dao.fetch(Platoon.class, "sF");
        p.setLeaderName("xyz");
        dao.update(p);

        p = dao.fetch(Platoon.class, "sF");
        String name = p.getLeaderName(); // xyz
        assertNotNull(name);

        p.setLeaderName(null);
        int re = dao.updateIgnoreNull(p);
        assertEquals(1, re);

        p = dao.fetch(Platoon.class, "sF");
        assertEquals(name, p.getLeaderName());

        p.setLeaderName(null);
        dao.update(p);
        p = dao.fetch(Platoon.class, "sF");
        assertNull(p.getLeaderName());

        p.setLeaderName("ABC");
        dao.update(p);
        p = dao.fetch(Platoon.class, "sF");
        assertEquals("ABC", p.getLeaderName());

        FieldFilter.create(Platoon.class, true).run(new Atom() {

            public void run() {
                System.out.println(FieldFilter.get(Platoon.class));
                Platoon p = dao.fetch(Platoon.class, "sF");
                p.setLeaderName(null);
                dao.update(p);
            }
        });
        p = dao.fetch(Platoon.class, "sF");
        assertEquals("ABC", p.getLeaderName());
    }

    @Test
    public void test_updateIgnoreNull_by_list() {
        pojos.initData();
        Platoon p = dao.fetch(Platoon.class, "sF");
        p.setLeaderName("xyz");
        dao.update(p);

        p = dao.fetch(Platoon.class, "sF");
        String name = p.getLeaderName(); // xyz
        assertNotNull(name);

        p.setLeaderName(null);
        int re = dao.updateIgnoreNull(Lang.list(p));
        assertEquals(1, re);

        p = dao.fetch(Platoon.class, "sF");
        assertEquals(name, p.getLeaderName());

        p.setLeaderName(null);
        dao.update(p);
        p = dao.fetch(Platoon.class, "sF");
        assertNull(p.getLeaderName());
    }

    @Test
    public void test_update_self_plus() {
        dao.create(Pet.class, true);
        Pet pet = Pet.create("Xy");
        pet.setAge(98);
        dao.insert(pet);
        pet = dao.fetch(Pet.class, (Cnd) null);
        dao.update(Pet.class, Chain.makeSpecial("age", "+1"), null);
        assertEquals(pet.getAge() + 1, dao.fetch(Pet.class, pet.getId())
                                          .getAge());
    }

    @Test
    public void test_update_with_pk_and_cnd() {
        dao.create(Pet.class, true);
        Pet pet = Pet.create("wendal");
        pet.setAge(30);
        dao.insert(pet);
        pet = dao.fetch(Pet.class, "wendal");
        pet.setAge(31);
        // 第一次更新, age符合要求
        dao.update(pet, FieldFilter.create(Pet.class, "age"), Cnd.where("age", "=", 30));
        // 第二次更新, age不符合要求
        pet.setAge(90);
        dao.update(pet, FieldFilter.create(Pet.class, "age"), Cnd.where("age", "=", 30));
        assertEquals(31, dao.fetch(Pet.class, "wendal").getAge());
    }
    
    @Test
    public void test_update_with_age_incr() {
        dao.create(Pet.class, true);
        Pet pet = Pet.create("wendal");
        pet.setAge(30);
        dao.insert(pet);
        final Pet pet2 = dao.fetch(Pet.class, "wendal");
        FieldFilter.create(Pet.class, true).run(new Atom() {
            public void run() {

                // 应该只有第一次生效
                dao.updateAndIncrIfMatch(pet2, null, "age");
                dao.updateAndIncrIfMatch(pet2, null, "age");
                dao.updateAndIncrIfMatch(pet2, null, "age");
                dao.updateAndIncrIfMatch(pet2, null, "age");
                dao.updateAndIncrIfMatch(pet2, null, "age");
                dao.updateAndIncrIfMatch(pet2, null, "age");
                assertEquals(31, dao.fetch(Pet.class, "wendal").getAge());
            }
        });

    }
    
    @Test
    public void test_update_with_version() {
        VersionTestPojo ttp = new VersionTestPojo();
        ttp.setName("wendal");
        ttp.setAge(20);
        
        dao.create(VersionTestPojo.class, true);
        dao.insert(ttp);
        ttp.setAge(30);
        dao.updateWithVersion(ttp);
        ttp.setAge(90);
        dao.updateWithVersion(ttp);
        assertEquals(30, dao.fetch(VersionTestPojo.class, "wendal").getAge());
    }
    
    @Test
    public void test_update_list_with_version() {
        dao.create(VersionTestPojo.class, true);
        List<VersionTestPojo> list = new ArrayList<VersionTestPojo>();
        VersionTestPojo ttp = new VersionTestPojo();
        ttp.setName("wendal");
        ttp.setAge(20);
        list.add(ttp);
        
        ttp = new VersionTestPojo();
        ttp.setName("wendal2");
        ttp.setAge(30);
        list.add(ttp);
        
        dao.insert(list);
        
        for (VersionTestPojo vtp : list) {
            vtp.setAge(40);
        }
        
        dao.updateWithVersion(list);
        //assertEquals(2, re);
        dao.updateWithVersion(list);
        assertEquals(40, dao.fetch(VersionTestPojo.class, "wendal").getAge());
        assertEquals(40, dao.fetch(VersionTestPojo.class, "wendal2").getAge());
    }
    
    @Test
    public void test_issue1260() {
        dao.update(Pet.class, Chain.makeSpecial("age", "+1").add("birthday", new Timestamp(System.currentTimeMillis())), null);
    }

    @Test
    public void test_update_clob() throws SerialException, SQLException {
        dao.create(UpdateClobBlobBean.class, true);
        UpdateClobBlobBean bean = new UpdateClobBlobBean();
        bean.setManytext(new SerialClob(Strings.dup('8', 4097).toCharArray()));
        bean.setManybinary(new SerialBlob(Strings.dup('9', 4097).getBytes()));
        dao.insert(bean);

        bean.setManytext(new SerialClob(Strings.dup('7', 4097).toCharArray()));
        bean.setManybinary(new SerialBlob(Strings.dup('6', 4097).getBytes()));

        dao.update(bean);
    }
}
