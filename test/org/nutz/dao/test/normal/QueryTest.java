package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;

public class QueryTest extends DaoCase {

	@Before
	public void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/meta/pet.dod"));
		// Insert 8 records
		for (int i = 0; i < 8; i++)
			dao.insert(Pet.create("pet" + i));
	}

	@Test
	public void query_by_pager() {
		List<Pet> pets = dao.query(Pet.class, Cnd.orderBy().asc("name"), dao.createPager(3, 2));
		assertEquals(2, pets.size());
		assertEquals("pet4", pets.get(0).getName());
		assertEquals("pet5", pets.get(1).getName());
	}

	@Test
	public void fetch_by_name() {
		Pet pet = dao.fetch(Pet.class, Cnd.where("name", "=", "pet2"));
		assertEquals("pet2", pet.getName());
	}
}
