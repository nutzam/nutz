package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.BeanWithDefault;
import org.nutz.dao.test.meta.Fighter;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.lang.Lang;

public class UpdateTest extends DaoCase {

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

        dao.update(    Pet.class,
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

        dao.update(Pet.class, Chain.make("name", "xiaobai"), Cnd.where("nickName", "=", "XB"));
        pet = dao.fetch(Pet.class, "xiaobai");
        assertEquals("XB", pet.getNickName());
    }

    @Test
    public void batch_update_all() {
        pojos.initData();
        dao.update(Fighter.class, Chain.make("type", Fighter.TYPE.SU_35.name()), null);
        assertEquals(    13,
                        dao.count(Fighter.class, Cnd.where("type", "=", Fighter.TYPE.SU_35.name())));
    }

    @Test
    public void batch_update_partly() {
        pojos.initData();
        int re = dao.update(Fighter.class,
                            Chain.make("type", "F15"),
                            Cnd.where("type", "=", "SU_35"));
        assertEquals(1, re);
        int maxId = dao.getMaxId(Fighter.class);
        re = dao.update(Fighter.class, Chain.make("type", "UFO"), Cnd.where("id", ">", maxId - 5));
        assertEquals(5, re);
        assertEquals(re, dao.count(Fighter.class, Cnd.where("type", "=", "UFO")));
    }

    @Test
    public void batch_update_relation() {
        pojos.initData();
        dao.updateRelation(    Fighter.class,
                            "base",
                            Chain.make("bname", "blue"),
                            Cnd.where("bname", "=", "red"));
        assertEquals(13, dao.count("dao_m_base_fighter", Cnd.where("bname", "=", "blue")));
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
        pet = dao.fetch(Pet.class, (Cnd)null);
        dao.update(Pet.class, Chain.makeSpecial("age", "+1"), null);
        assertEquals(pet.getAge() + 1, dao.fetch(Pet.class, pet.getId()).getAge());
    }
}
