package com.zzh.dao.test.entity;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zzh.dao.Database;
import com.zzh.dao.TableName;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.Link;
import com.zzh.dao.test.meta.Tank;

public class DynamicEntityParsing {

	@Test
	public void tank_many_many_link_test() {
		Entity<Tank> en = new Entity<Tank>();
		assertTrue(en.parse(Tank.class, new Database.Unknwon()));

		Link link = en.getManyManys().get("members");
		TableName.set(1);
		assertEquals("dao_d_m_soldier_tank_1", link.getRelation());
		assertEquals("id", link.getReferField().getName());
		assertEquals("name", link.getTargetField().getName());
		TableName.clear();
	}

}
