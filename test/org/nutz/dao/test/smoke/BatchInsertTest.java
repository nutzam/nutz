package org.nutz.dao.test.smoke;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
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

}
