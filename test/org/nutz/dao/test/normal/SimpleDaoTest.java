package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;

public class SimpleDaoTest extends DaoCase {

	@Before
	public void before() {
		Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/meta/pet.dod"));
	}

	private void insertRecords(int len) {
		for (int i = 0; i < len; i++) {
			Pet pet = Pet.create("pet" + i);
			pet.setNickName("alias_" + i);
			dao.insert(pet);
		}
	}

	@Test
	public void test_count_with_entity() {
		insertRecords(8);
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

	@Test
	public void test_count_by_condition() {
		insertRecords(4);
		assertEquals(4,dao.count(Pet.class));
		assertEquals(2, dao.count(Pet.class, Cnd
				.wrap("name IN ('pet2','pet3') ORDER BY name ASC)")));
	}

}
