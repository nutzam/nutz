package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Stabber;
import org.nutz.dao.test.meta.StabberSeq;

public class AutoGenerateValueTest extends DaoCase {

	@Override
	protected void before() {
		Sqls.executeDefinitionFile(dao, "org/nutz/dao/test/meta/stabber.dod");
	}

	@Override
	protected void after() {}

	@Test
	public void test_create_seq_value() {
		/*
		 * MS-SqlServer don't support Subqueries, so, skip the test for
		 * MS-SqlServer
		 */
		if (dao.meta().isSqlServer()) {
			assertTrue(true);
		} else {
			StabberSeq seq = new StabberSeq();
			seq.setValue(10);
			dao.insert(seq);

			Stabber s = new Stabber();
			s.setName("A");

			dao.insert(s);
			s = dao.fetch(s);
			assertEquals(10, s.getCaseNumber());

			seq.setValue(200);
			dao.update(seq);

			s.setName("B");
			dao.insert(s);
			s = dao.fetch(s);
			assertEquals(200, s.getCaseNumber());
		}
	}

}
