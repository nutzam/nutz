package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.test.DaoCase;

public class InsertTest extends DaoCase {

	@Test
	public void test_insert_by_el() {
		dao.create(Pet3.class, false);
		Pet3 p = new Pet3();
		dao.insert(p);
		assertTrue(p.getId() > 0);
		assertTrue(p.getName().startsWith("N_"));
	}

}
