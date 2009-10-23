package org.nutz.dao.test.entity;

import org.junit.Test;

import static org.junit.Assert.*;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.entity.impl.DefaultEntityMaker;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.dao.test.meta.Stabber;
import org.nutz.ioc.meta.Obj;

public class EntityParsing {

	private static Entity en(Class<?> type) {
		return new DefaultEntityMaker().make(new DatabaseMeta(), type);
	}

	@Test
	public void eval_manys() {
		Entity en = en(Base.class);
		Link link = en.getLinks("platoons").get(0);
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.Platoon", link.getTargetClass().getName());
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("name", link.getReferField().getName());
		assertEquals("baseName", link.getTargetField().getName());
	}

	@Test
	public void eval_manys_with_null_field() {
		Entity en = en(Base.class);
		Link link = en.getLinks("wavebands").get(0);
		assertEquals("wavebands", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.WaveBand", link.getTargetClass().getName());
		assertNull(link.getReferField());
		assertNull(link.getTargetField());
	}

	@Test
	public void eval_manymany() {
		Entity en = en(Base.class);
		Link link = en.getLinks("fighters").get(0);
		assertEquals("dao_m_base_fighter", link.getRelation());
	}

	@Test
	public void eval_id_name() {
		Entity en = en(Platoon.class);
		assertEquals("id", en.getIdField().getName());
		assertEquals("name", en.getNameField().getName());
	}

	@Test
	public void eval_obj_id_name() {
		Entity en = en(Obj.class);
		assertEquals("id", en.getIdField().getName());
		assertEquals("name", en.getNameField().getName());
	}

	@Test
	public void test_field_with_next_ann() {
		Entity en = en(Stabber.class);
		assertNotNull(en.getField("caseNumber").getNextIntQuerySql());
	}
}
