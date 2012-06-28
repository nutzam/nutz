package org.nutz.dao.test.smoke;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.TableName;
import org.nutz.dao.test.DaoCase;

public class TableNameTest extends DaoCase {

    @Before
    public void before() {
        TableName.set("t_pet");
        dao.create(DPet.class, true);
        TableName.clear();
    }

    @Test
    public void test_insert_DPet() {
        DPet pet = new DPet();
        pet.setName("XiaoBai");
        pet.setAge(10);

        TableName.set("t_pet");
        try {
            dao.insert(pet);
            assertTrue(pet.getId() > 0);
            assertEquals(1, dao.count(DPet.class));
        }
        finally {
            TableName.clear();
        }
    }
    
}
