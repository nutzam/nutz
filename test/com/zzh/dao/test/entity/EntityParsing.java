package com.zzh.dao.test.entity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.zzh.dao.Database;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.Link;
import com.zzh.dao.test.meta.Base;

public class EntityParsing {

	private Entity<Base> en;

	@Before
	public void setUp() {
		en = new Entity<Base>();
		en.parse(Base.class,new Database.Unknwon());
	}

	@Test
	public void eval_manys() {
		Link link = en.getManys().get("platoons");
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("com.zzh.dao.test.meta.Platoon", link.getTargetClass().getName());
		assertEquals("platoons", link.getOwnField().getName());
		assertEquals("name", link.getReferField().getName());
		assertEquals("baseName", link.getTargetField().getName());
	}

	@Test
	public void eval_manys_with_null_field() {
		Link link = en.getManys().get("wavebands");
		assertEquals("wavebands", link.getOwnField().getName());
		assertEquals("com.zzh.dao.test.meta.WaveBand", link.getTargetClass().getName());
		assertNull(link.getReferField());
		assertNull(link.getTargetField());
	}

	@Test
	public void eval_manymany() {
		Link link = en.getManyManys().get("fighters");
		assertEquals("dao_m_base_fighter", link.getRelation());
	}
	

}
