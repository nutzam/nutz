package org.nutz.dao.impl.sql;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoMaker;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Master;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Lang;

/**
 * @Author: Haimming
 * @Date: 2019-12-19 10:24
 * @Version 1.0
 */
public class NutPojoMakerTest extends DaoCase {
    private <T> Entity<T> en(Class<T> type) {
        return dao.getEntity(type);
    }

    @Test
    public void makeQueryByJoin() {
        dao.create(Pet.class, true);
        dao.create(Master.class, true);
        Master master = new Master();
        master.setName("zozoh");

        Pet petA = new Pet();
        petA.setName("wendal");
        petA.setAge(31);
        Pet petB = new Pet();
        petB.setName("pangwu");
        petB.setAge(30);
        master.setPets(Arrays.asList(petA, petB));
        dao.insertWith(master, null);
        PojoMaker pojoMaker = new NutPojoMaker(dao.getJdbcExpert());
        Entity<?> en = en(Master.class);
        Pojo pojo = pojoMaker.makeQueryByJoin(en, "pets");
        assertEquals(true, Lang.isNotEmpty(pojo));
    }
}