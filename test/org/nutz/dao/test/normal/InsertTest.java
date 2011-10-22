package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.trans.Atom;

public class InsertTest extends DaoCase {

	@Test
	public void test_insert_by_fieldfilter() {
		dao.create(Pet.class, true);
		final Pet pet = Pet.create("xb");
		pet.setNickName("xiaobai");
		FieldFilter.create(Pet.class, "^id|name$").run(new Atom() {
			public void run() {
				dao.insert(pet);
			}
		});
		Pet xb = dao.fetch(Pet.class, "xb");
		assertNull(xb.getNickName());
	}

	@Test
	public void test_insert_by_el() {
		dao.create(Pet3.class, false);
		Pet3 p = new Pet3();
		dao.insert(p);
		assertTrue(p.getId() > 0);
		assertTrue(p.getName().startsWith("N_"));
	}

}
