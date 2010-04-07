package org.nutz.dao.test.smoke;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.TableName;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;

public class TableNameTest extends DaoCase {

	@Before
	public void before() {
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/meta/pet.dod"));
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
