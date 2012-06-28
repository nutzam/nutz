package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Each;

public class QueryTest extends DaoCase {

    public void before() {
        dao.create(Pet.class, true);
        // Insert 8 records
        for (int i = 0; i < 8; i++)
            dao.insert(Pet.create("pet" + i));
    }

    @Test
    public void test_record_to_entity() {
        dao.each(Pet.class, null, new Each<Pet>() {
            public void invoke(int index, Pet pet, int length) {
                pet.setNickName("AA_" + pet.getName().toUpperCase());
                dao.update(pet);
            }
        });
        Entity<Pet> en = dao.getEntity(Pet.class);
        Sql sql = Sqls.queryRecord("SELECT * FROM t_pet");
        dao.execute(sql);
        List<Record> recs = sql.getList(Record.class);
        Pet[] pets = new Pet[recs.size()];
        int i = 0;
        for (Record rec : recs)
            pets[i++] = rec.toEntity(en);

        for (Pet pet : pets)
            assertEquals("AA_" + pet.getName().toUpperCase(), pet.getNickName());
    }

    /**
     * Github Issue #101
     */
    @Test
    public void query_by_cri_equals_null() {
        Criteria cri = Cnd.cri();
        cri.where().andEquals("name", null);
        List<Pet> pets = dao.query(Pet.class, cri, null);
        assertEquals(0, pets.size());

        cri = Cnd.cri();
        cri.where().andNotEquals("name", null);
        pets = dao.query(Pet.class, cri, null);
        assertEquals(8, pets.size());
    }

    @Test
    public void query_by_wrap_null() {
        List<Pet> pets = dao.query(Pet.class, Cnd.wrap(null), null);
        assertEquals(8, pets.size());
    }

    @Test
    public void query_by_orderbyset() {
        List<Pet> pets = dao.query(Pet.class, Cnd.cri().asc("id"), null);
        assertEquals(8, pets.size());
    }

    @Test
    public void query_by_map_idkeyset() {
        List<Pet> pets = dao.query(Pet.class, null, null);
        Map<Integer, Pet> map = new HashMap<Integer, Pet>();
        map.put(pets.get(2).getId(), null);
        map.put(pets.get(4).getId(), null);
        pets = dao.query(Pet.class, Cnd.where("id", "in", map.keySet()), null);
        assertEquals(2, pets.size());
    }

    @Test
    public void query_by_map_namekeyset() {
        Map<String, Pet> map = new HashMap<String, Pet>();
        map.put("pet3", null);
        map.put("pet5", null);
        List<Pet> pets = dao.query(Pet.class, Cnd.where("name", "in", map.keySet()), null);
        assertEquals(2, pets.size());
    }

    @Test
    public void query_by_int_range() {
        int maxId = dao.getMaxId(Pet.class);
        List<Pet> pets = dao.query(    Pet.class,
                                    Cnd.where("id", "IN", new int[]{maxId, maxId - 1}),
                                    null);
        assertEquals(2, pets.size());
    }

    @Test
    public void clear_by_int_range() {
        int maxId = dao.getMaxId(Pet.class);
        int num = dao.clear(Pet.class, Cnd.where("id", "IN", new int[]{maxId, maxId - 1}));
        assertEquals(2, num);
    }

    @Test
    public void query_by_long_range() {
        int maxId = dao.getMaxId(Pet.class);
        List<Pet> pets = dao.query(    Pet.class,
                                    Cnd.where("id", "IN", new long[]{maxId, maxId - 1}),
                                    null);
        assertEquals(2, pets.size());
    }

    @Test
    public void query_by_special_char() {
        dao.update(dao.fetch(Pet.class).setName("a@b"));
        List<Pet> pets = dao.query(Pet.class, Cnd.where("name", "=", "a@b"), null);
        assertEquals(1, pets.size());
    }

    @Test
    public void query_by_special_char2() {
        dao.update(dao.fetch(Pet.class).setName("a$b"));
        List<Pet> pets = dao.query(Pet.class, Cnd.where("name", "=", "a$b"), null);
        assertEquals(1, pets.size());
    }

    @Test
    public void query_by_pager() {
        List<Pet> pets = dao.query(Pet.class, Cnd.orderBy().asc("name"), dao.createPager(3, 2));
        assertEquals(2, pets.size());
        assertEquals("pet4", pets.get(0).getName());
        assertEquals("pet5", pets.get(1).getName());
    }

    @Test
    public void query_by_like() {
        List<Pet> pets = dao.query(    Pet.class,
                                    Cnd.where("name", "LIKE", "6"),
                                    dao.createPager(1, 10));
        assertEquals(1, pets.size());
        assertEquals("pet6", pets.get(0).getName());
    }

    @Test
    public void query_by_like_ignorecase() {
        SimpleCriteria cri = Cnd.cri();
        cri.where().andLike("name", "PeT6", true);
        List<Pet> pets = dao.query(Pet.class, cri, dao.createPager(1, 10));
        assertEquals(1, pets.size());
        assertEquals("pet6", pets.get(0).getName());
    }

    @Test
    public void fetch_by_name() {
        Pet pet = dao.fetch(Pet.class, Cnd.where("name", "=", "pet2"));
        assertEquals("pet2", pet.getName());
    }

    @Test
    public void query_records() {
        List<Record> pets = dao.query("t_pet", Cnd.orderBy().asc("name"), null);
        assertEquals(8, pets.size());
        assertEquals("pet0", pets.get(0).get("name"));
        assertEquals("pet7", pets.get(7).get("name"));
    }

    @Test
    public void query_records_pager() {
        List<Record> pets = dao.query("t_pet:id", Cnd.orderBy().asc("name"), dao.createPager(3, 2));
        assertEquals(2, pets.size());
        assertEquals("pet4", pets.get(0).get("name"));
        assertEquals("pet5", pets.get(1).get("name"));
    }

    @Test
    public void fetch_record() {
        Record re = dao.fetch("t_pet", Cnd.where("name", "=", "pet3"));
        Pet pet = re.toPojo(Pet.class);
        assertEquals(6, re.getColumnCount());
        assertEquals(4, pet.getId());
        assertEquals("pet3", pet.getName());
    }

    @Test
    public void query_records_pager_new() {
        List<Record> pets = dao.query("t_pet:id", Cnd.limit().limit(3, 2).asc("name"));
        assertEquals(2, pets.size());
        assertEquals("pet4", pets.get(0).get("name"));
        assertEquals("pet5", pets.get(1).get("name"));
    }
}
