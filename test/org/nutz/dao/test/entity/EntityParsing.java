package org.nutz.dao.test.entity;

import org.junit.Test;

import static org.junit.Assert.*;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;
import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;

public class EntityParsing extends DaoCase {

	private Entity<?> en(Class<?> type) {
		return dao.getEntity(type);
	}

	@Test
	public void eval_manys() {
		Entity<?> en = en(Base.class);
		Link link = en.getLinks("platoons").get(0);
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.Platoon", link.getTargetClass().getName());
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("name", link.getReferField().getName());
		assertEquals("baseName", link.getTargetField().getName());
	}

	@Test
	public void eval_manys_with_null_field() {
		Entity<?> en = en(Base.class);
		Link link = en.getLinks("wavebands").get(0);
		assertEquals("wavebands", link.getOwnField().getName());
		assertEquals("org.nutz.dao.test.meta.WaveBand", link.getTargetClass().getName());
		assertNull(link.getReferField());
		assertNull(link.getTargetField());
	}

	@Test
	public void eval_manymany() {
		Entity<?> en = en(Base.class);
		Link link = en.getLinks("fighters").get(0);
		assertEquals("dao_m_base_fighter", link.getRelation());
	}

	@Test
	public void eval_id_name() {
		Entity<?> en = en(Platoon.class);
		assertEquals("id", en.getIdField().getName());
		assertEquals("name", en.getNameField().getName());
	}

	@Test
	public void test_pk_multiple() {
		Entity<?> en = en(TO0.class);
		assertEquals(2, en.getPkFields().length);
		assertEquals("to0", en.getViewName());
		assertEquals("id", en.getPkFields()[0].getName());
		assertEquals("name", en.getPkFields()[1].getName());
		assertNull(en.getIdField());
		assertNull(en.getNameField());

		assertTrue(en.getField("id").isPk());
		assertTrue(en.getField("name").isPk());
	}

	@Test
	public void test_pk_id() {
		Entity<?> en = en(TO1.class);
		assertNull(en.getPkFields());
		assertEquals("to1", en.getViewName());
		assertEquals("id", en.getIdField().getName());
		assertEquals("name", en.getNameField().getName());

		assertTrue(en.getField("id").isPk());
		assertFalse(en.getField("name").isPk());
	}

	@Test
	public void test_pk_name() {
		Entity<?> en = en(TO2.class);
		assertNull(en.getPkFields());
		assertEquals("to2", en.getViewName());
		assertEquals("id", en.getIdField().getName());
		assertEquals("name", en.getNameField().getName());

		assertFalse(en.getField("id").isPk());
		assertTrue(en.getField("name").isPk());
	}

	@Test
	public void test_pk_order() {
		Entity<?> en = en(TO4.class);
		assertEquals(2, en.getPkFields().length);
		assertEquals("to4", en.getViewName());
		assertEquals("masterId", en.getPkFields()[0].getName());
		assertEquals("id", en.getPkFields()[1].getName());
		assertNull(en.getIdField());
		assertNull(en.getNameField());

		assertTrue(en.getField("masterId").isPk());
		assertTrue(en.getField("id").isPk());
	}

	@Test
	public void test_complex_pojo_without_db() {
		Entity<?> en = en(TO5.class);
		assertEquals("toid", en.getField("id").getColumnName());
	}
}
