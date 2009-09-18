package org.nutz.dao.test.entity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.test.meta.Base;

public class EntityParsing {

	private Entity<Base> en;

	@Before
	public void setUp() {
		en = new Entity<Base>();
		en.parse(Base.class, new DatabaseMeta());
	}

	@Test
	public void eval_manys() {
		Link link = en.getManys().get("platoons");
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.Platoon", link.getTargetClass().getName());
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("name", link.getReferField().getName());
		assertEquals("baseName", link.getTargetField().getName());
	}

	@Test
	public void eval_manys_with_null_field() {
		Link link = en.getManys().get("wavebands");
		assertEquals("wavebands", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.WaveBand", link.getTargetClass().getName());
		assertNull(link.getReferField());
		assertNull(link.getTargetField());
	}

	@Test
	public void eval_manymany() {
		Link link = en.getManyManys().get("fighters");
		assertEquals("dao_m_base_fighter", link.getRelation());
	}

}
