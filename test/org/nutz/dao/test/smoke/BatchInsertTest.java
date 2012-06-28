package org.nutz.dao.test.smoke;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.trans.Atom;

public class BatchInsertTest extends DaoCase {

    @Before
    public void before() {
        dao.create(Pet.class, true);
    }

    @Test
    public void test_insert_3_pets() {
        assertEquals(0, dao.count(Pet.class));
        dao.insert(Pet.create(3));
        assertEquals(3, dao.count(Pet.class));

        assertTrue(null != dao.fetch(Pet.class, 1));
        assertTrue(null != dao.fetch(Pet.class, 2));
        assertTrue(null != dao.fetch(Pet.class, 3));

        FieldFilter.create(Pet.class, "name|id").run(new Atom() {
            public void run() {
                assertTrue(null != dao.fetch(Pet.class));
            }
        });
    }

    /**
     * For Issue #118
     */
    @Test
    public void test_insert_2_pets_by_chain_to_map() {
        dao.insert(Chain.make(".table", "t_pet").add("name", "A").toMap());
        dao.insert(Chain.make(".table", "t_pet").add("name", "B").toMap());

        List<Pet> pets = dao.query(Pet.class, Cnd.orderBy().asc("name"), null);
        assertEquals(2, pets.size());
        assertEquals("A", pets.get(0).getName());
        assertEquals("B", pets.get(1).getName());
    }

    /**
     * For Issue #118
     */
    @Test
    public void test_fast_insert_2_pets_by_chain_to_map() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list.add(Chain.make(".table", "t_pet").add("name", "A").toMap());
        list.add(Chain.make(".table", "t_pet").add("name", "B").toMap());

        dao.fastInsert(list);

        List<Pet> pets = dao.query(Pet.class, Cnd.orderBy().asc("name"), null);
        assertEquals(2, pets.size());
        assertEquals("A", pets.get(0).getName());
        assertEquals("B", pets.get(1).getName());
    }

}
