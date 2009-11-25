package org.nutz.dao.test.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Tank;

public class DynamicEntityParsing extends DaoCase {
	
	@Test
	public void tank_many_many_link_test() {
		pojos.initPlatoon(1);
		TableName.set(1);
		Entity<?> en = dao.getEntity(Tank.class);
		Link link = en.getLinks("members").get(0);
		assertEquals("dao_d_m_soldier_tank_1", link.getRelation());
		assertEquals("id", link.getReferField().getName());
		assertEquals("name", link.getTargetField().getName());
		TableName.clear();
		pojos.dropPlatoon(1);
	}

}
