package org.nutz.dao.test.normal;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.XPet;


public class DaoRecordTest extends DaoCase {

	@Test
	public void test_null_timestamp() {
		dao.create(XPet.class, true);
		dao.insert(new XPet());
		
		assertEquals(1, dao.count(XPet.class));
		assertNotNull(dao.fetch(XPet.class));
		
		Sql sql = Sqls.fetchEntity("select * from t_xpet");
		sql.setEntity(dao.getEntity(XPet.class));
		dao.execute(sql);
		assertNotNull(sql.getObject(XPet.class));
		assertNull(sql.getObject(XPet.class).getOtherTime());
	}
}
