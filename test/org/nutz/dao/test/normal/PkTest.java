package org.nutz.dao.test.normal;

import org.junit.Before;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Pet;
import org.nutz.dao.tools.Tables;

public class PkTest extends DaoCase {

	@Before
	public void before() {
		Tables.run(dao, Tables.define("org/nutz/dao/test/meta/pet.dod"));
		// Insert 8 records
		for (int i = 0; i < 8; i++) {
			Pet pet = Pet.create("pet" + i);
			pet.setNickName("alias_" + i);
			dao.insert(pet);
		}
	}

}
