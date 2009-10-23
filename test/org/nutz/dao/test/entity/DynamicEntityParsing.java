package org.nutz.dao.test.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.entity.impl.DefaultEntityMaker;
import org.nutz.dao.test.meta.Tank;

public class DynamicEntityParsing {

	@Test
	public void tank_many_many_link_test() {
		Entity en = new DefaultEntityMaker().make(new DatabaseMeta(),Tank.class);

		Link link = en.getLinks("members").get(0);
		TableName.set(1);
		assertEquals("dao_d_m_soldier_tank_1", link.getRelation());
		assertEquals("id", link.getReferField().getName());
		assertEquals("name", link.getTargetField().getName());
		TableName.clear();
	}

}
