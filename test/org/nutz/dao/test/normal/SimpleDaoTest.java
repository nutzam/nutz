package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.test.meta.PetObj;
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
	public void test_fetch_by_condition_in_special_char() {
		if (dao.exists(Pet.class)) {
			Tables.define(dao, Tables.loadFrom("org/nutz/dao/test/meta/pet.dod"));
			dao.clear(Pet.class);
			dao.insert(Pet.create("a@b"));
		}
		Pet pet = dao.fetch(Pet.class, Cnd.where("name", "=", "a@b"));
		assertEquals("a@b", pet.getName());
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
		assertEquals(4, dao.count(Pet.class));
		assertEquals(	2,
						dao.count(Pet.class, Cnd.wrap("name IN ('pet2','pet3') ORDER BY name ASC)")));
	}

	@Test
	public void run_2_sqls_with_error() {
		assertEquals(0, dao.count(Pet.class));
		Sql sql1 = Sqls.create("INSERT INTO t_pet (name) VALUES ('A')");
		Sql sql2 = Sqls.create("INSERT INTO t_pet (nocol) VALUES ('B')");
		try {
			dao.execute(sql1, sql2);
			fail();
		}
		catch (DaoException e) {}
		assertEquals(0, dao.count(Pet.class));
	}

	@Test
	public void test_clear_two_records() {
		dao.insert(Pet.create("A"));
		dao.insert(Pet.create("B"));
		assertEquals(2, dao.clear(Pet.class, Cnd.where("id", ">", 0)));
		assertEquals(0, dao.clear(Pet.class, Cnd.where("id", ">", 0)));
	}

	@Test
	public void test_delete_records() {
		dao.insert(Pet.create("A"));
		dao.insert(Pet.create("B"));
		assertEquals(1, dao.delete(Pet.class, "A"));
		assertEquals(1, dao.delete(Pet.class, "B"));
		assertEquals(0, dao.delete(Pet.class, "A"));
	}

	@Test
	public void test_integer_object_column() {
		dao.insert(PetObj.create("X"));
		PetObj pet = dao.fetch(PetObj.class, "X");

		assertEquals("X", pet.getName());
		assertNull(pet.getAge());

		dao.update(pet.setAge(20));
		pet = dao.fetch(PetObj.class, "X");
		assertEquals(20, pet.getAge().intValue());

		dao.update(pet.setAge(null));
		pet = dao.fetch(PetObj.class, "X");
		assertNull(pet.getAge());
	}
}
