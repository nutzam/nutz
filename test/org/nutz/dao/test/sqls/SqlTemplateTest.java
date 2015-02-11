package org.nutz.dao.test.sqls;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.sql.SqlTemplate;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.lang.Lang;

public class SqlTemplateTest extends DaoCase {

    private SqlTemplate sqlTemplate;

    @Override
    protected void before() {
        if (sqlTemplate == null)
            sqlTemplate = new SqlTemplate(dao);
    }

    @Test
    public void testUpdate() {
        pojos.initPet();
        dao.insert(Pet.create(1));
        String sql = "UPDATE $table SET name=@name";

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("table", "t_pet");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("name", "Peter");
        sqlTemplate.update(sql, vars, param);

        List<Pet> pets = dao.query(Pet.class, null);

        assertEquals("Peter", pets.get(0).getName());
    }

    @Test
    public void testBatchUpdate() {
        pojos.initPet();

        List<Map<String, Object>> batchValues = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> map = Lang.map(String.format("'name':'Pet_%d'", i));
            batchValues.add(map);
        }
        String sql = "INSERT INTO t_pet(name) VALUES(@name)";

        sqlTemplate.batchUpdate(sql, null, batchValues);

        assertEquals(5, sqlTemplate.queryForInt("SELECT COUNT(*) FROM t_pet", null));
    }

    @Test
    public void testQueryForInt() {
        pojos.initPet();
        dao.insert(Pet.create(1));
        int petCount = sqlTemplate.queryForInt("SELECT COUNT(*) FROM t_pet", null);
        assertEquals(1, petCount);

        petCount = sqlTemplate.queryForInt("SELECT COUNT(*) FROM t_pet WHERE 1=2 ", null);
        assertEquals(0, petCount);

    }

    @Test
    public void testQueryForLong() {
        pojos.initPet();
        dao.insert(Pet.create(1));
        long petCount = sqlTemplate.queryForLong("SELECT COUNT(*) FROM t_pet", null);
        assertEquals(1, petCount);

        petCount = sqlTemplate.queryForLong("SELECT COUNT(*) FROM t_pet WHERE 1=2 ", null);
        assertEquals(0, petCount);

    }

    @Test
    public void testQueryForObjectClassOfT() {
        pojos.initPet();
        Pet pet = Pet.create("papa");
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        pet.setBirthday(createTime);

        dao.insert(pet);
        String sql = "SELECT birthday FROM t_pet";
        Timestamp dbCreateTime = sqlTemplate.queryForObject(sql, null, Timestamp.class);
        assertEquals(createTime.getTime() / 1000 , dbCreateTime.getTime() / 1000);

        String sql1 = "SELECT birthday FROM t_pet WHERE 1=2";
        dbCreateTime = sqlTemplate.queryForObject(sql1, null, Timestamp.class);
        assertTrue(dbCreateTime == null);
    }

    @Test
    public void testQueryForObjectEntityOfT() {
        pojos.initPet();
        Pet pet = Pet.create("papa");
        dao.insert(pet);

        String sql = "SELECT * FROM t_pet";
        Pet p2 = sqlTemplate.queryForObject(sql, null, dao.getEntity(Pet.class));
        assertEquals(pet.getName(), p2.getName());

        String sql1 = "SELECT * FROM t_pet WHERE 1=2";
        Pet p3 = sqlTemplate.queryForObject(sql1, null, dao.getEntity(Pet.class));
        assertTrue(p3 == null);
    }

    @Test
    public void testQueryForRecord() {
        pojos.initPet();
        Pet pet = Pet.create("papa");
        dao.insert(pet);

        String sql = "SELECT name,age FROM $table WHERE id = @id";

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("table", "t_pet");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", pet.getId());

        Record re = sqlTemplate.queryForRecord(sql, vars, params);

        assertEquals(pet.getName(), re.getString("name"));
    }

    @Test
    public void testQueryEntity1() {
        pojos.initPet();
        dao.insert(Pet.create(4));

        String sql = "SELECT * FROM t_pet";
        List<Pet> pets = sqlTemplate.query(sql, null, dao.getEntity(Pet.class));

        assertEquals(4, pets.size());

        assertEquals("pet_00", pets.get(0).getName());
    }

    @Test
    public void testQueryEntity2() {
        pojos.initPet();
        dao.insert(Pet.create(4));

        String sql = "SELECT * FROM t_pet";
        List<Pet> pets = sqlTemplate.query(sql, null, Pet.class);

        assertEquals(4, pets.size());

        assertEquals("pet_00", pets.get(0).getName());
    }

    @Test
    public void testQueryForList() {
        pojos.initPet();
        dao.insert(Pet.create(4));

        String sql = "SELECT name FROM t_pet";

        List<String> names = sqlTemplate.queryForList(sql, null, null, String.class);

        assertTrue(names.contains("pet_00"));

        String sql1 = "SELECT name FROM t_pet WHERE 1=2";

        names = sqlTemplate.queryForList(sql1, null, null, String.class);

        assertTrue(names.isEmpty());
    }

    @Test
    public void testQueryRecords() {
        pojos.initPet();
        dao.insert(Pet.create(4));

        String sql = "SELECT name FROM t_pet";

        List<Record> res = sqlTemplate.queryRecords(sql, null, null);

        assertEquals("pet_00", res.get(0).getString("name"));

    }

    @Test
    public void testSqlInExp() {
        pojos.initPet();
        dao.insert(Pet.create(4));

        String sql = "SELECT name FROM t_pet WHERE id IN (@ids)";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ids", Lang.array(1, 2, 3, 4));

        List<String> names = sqlTemplate.queryForList(sql, null, params, String.class);
        assertTrue(names.size() == 4);
        assertTrue(names.contains("pet_00"));

        params = new HashMap<String, Object>();
        params.put("ids", Lang.list(1, 2, 3, 4));

        names = sqlTemplate.queryForList(sql, null, params, String.class);
        assertTrue(names.size() == 4);
        assertTrue(names.contains("pet_00"));

    }
}
