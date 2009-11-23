package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nutz.dao.FieldFilter;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;
import org.nutz.trans.Atom;

public class FieldFilterTest extends DaoCase {

	private static Pet pet(String name) {
		Pet p = new Pet();
		p.setName(name);
		return p;
	}

	@Override
	protected void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/meta/pet.dod"));
		dao.insert(pet("xb"));
	}

	@Test
	public void test_insert_by_filter() {
		// insert one pet
		final Pet p = pet("xh").setNickName("XiaoHei");
		FieldFilter.create(Pet.class, "id|name").run(new Atom() {
			public void run() {
				dao.insert(p);
			}
		});
		Pet p2 = dao.fetch(Pet.class, p.getId());
		assertNull(p2.getNickName());
	}

	@Test
	public void test_update_by_filter() {
		final Pet p = dao.fetch(Pet.class, "xb");
		p.setNickName("XiaoBai");
		FieldFilter.create(Pet.class, "id|name").run(new Atom() {
			public void run() {
				dao.update(p);
			}
		});
		Pet p2 = dao.fetch(Pet.class, p.getId());
		assertNull(p2.getNickName());
	}

	@Test
	public void test_select_by_filter() {
		dao.update(dao.fetch(Pet.class, "xb").setNickName("XiaoBai"));
		assertEquals("XiaoBai", dao.fetch(Pet.class, "xb").getNickName());
		final Pet[] pets = new Pet[1];
		FieldFilter.create(Pet.class, "id|name").run(new Atom() {
			public void run() {
				pets[0] = dao.fetch(Pet.class, "xb");
			}
		});
		assertNull(pets[0].getNickName());
	}

	@Test
	public void test_query_by_filter() {
		dao.update(dao.fetch(Pet.class, "xb").setNickName("XiaoBai"));
		assertEquals("XiaoBai", dao.fetch(Pet.class, "xb").getNickName());
		final List<Pet> pets = new ArrayList<Pet>();
		FieldFilter.create(Pet.class, "id|name").run(new Atom() {
			public void run() {
				pets.add(dao.query(Pet.class, null, null).get(0));
			}
		});
		assertNull(pets.get(0).getNickName());
	}

}
