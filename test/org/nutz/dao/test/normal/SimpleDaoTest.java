package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;

public class SimpleDaoTest extends DaoCase {

	@Before
	public void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/meta/pet.dod"));
	}

	private void insert8Records() {
		// Insert 8 records
		for (int i = 0; i < 8; i++) {
			Pet pet = Pet.create("pet" + i);
			pet.setNickName("alias_" + i);
			dao.insert(pet);
		}
	}

	@Test
	public void test_count_with_entity() {
		insert8Records();
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
	
}
