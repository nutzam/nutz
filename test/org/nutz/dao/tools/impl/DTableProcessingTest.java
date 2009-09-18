package org.nutz.dao.tools.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Chain;
import org.nutz.dao.Sqls;
import org.nutz.dao.test.DaoCase;

public class DTableProcessingTest extends DaoCase {

	@Test
	public void create_static_pk() {
		Sqls.execute(dao, "DAO_TOOL_ABC{id INT PK, name VARCHAR(20)}");
		dao.insert("DAO_TOOL_ABC", Chain.make("id", 99).add("name", "ABC"));
		assertEquals(1,dao.count("DAO_TOOL_ABC"));
		assertEquals(99,dao.func("DAO_TOOL_ABC", "MAX", "id"));
	}

}
