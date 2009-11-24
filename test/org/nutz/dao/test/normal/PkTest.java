package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.tools.Tables;

public class PkTest extends DaoCase {

	@Before
	public void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/normal/dog.dod"));
		// Insert 8 records
		for (int i = 0; i < 8; i++) {
			Dog dog = new Dog();
			dog.setMasterId(32);
			dog.setId(i + 1);
			dog.setName("dog_" + i);
			dog.setAge((i + 10) / 2);
			dao.insert(dog);
		}
	}

	@Test
	public void test_fetch_by_pks() {
		Dog dog = dao.fetchx(Dog.class, 32, 3);
		assertEquals("dog_2", dog.getName());
	}

	@Test
	public void test_delete_by_pks() {
		assertEquals(8, dao.count(Dog.class));
		dao.deletex(Dog.class, 32, 5);
		assertEquals(7, dao.count(Dog.class));
	}

	@Test
	public void test_update() {
		Dog dog = dao.fetchx(Dog.class, 32, 3);
		dog.setName("XiaoBai");
		dao.update(dog);

		dog = dao.fetchx(Dog.class, 32, 3);
		assertEquals("XiaoBai", dog.getName());
	}

}
