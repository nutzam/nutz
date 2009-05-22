package com.zzh.ioc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zzh.dao.test.meta.Base;
import com.zzh.dao.test.meta.Platoon;
import com.zzh.ioc.json.JsonMappingLoader;

public class JsonIocAdvanceTest {

	private Nut nut;

	@Before
	public void setUp() throws Exception {
		nut = new Nut(new JsonMappingLoader("com/zzh/ioc/ioc.js"));
	}

	@After
	public void tearDown() throws Exception {
		if (null != null)
			nut.depose();
	}

	@Test
	public void test_deposer() {
		Base b1 = nut.get(Base.class, "blue");
		Base b2 = nut.get(Base.class, "blue");
		assertTrue(b1 == b2);
		assertEquals("Blue", b1.getName());
		assertEquals("United States", b1.getCountry().getName());
		nut.depose();
		assertEquals("!!!", b1.getName());
		assertEquals("#", b1.getCountry().getName());
		assertTrue(b1.getPlatoons().get("DF") == b1.getPlatoons().get("DF2"));
	}

	@Test
	public void test_cascade_unsingleton_injection() {
		Base b1 = nut.get(Base.class, "red");
		Base b2 = nut.get(Base.class, "red");
		assertFalse(b1 == b2);
		assertEquals("China", b1.getCountry().getName());
		assertEquals("China", b2.getCountry().getName());
		assertFalse(b1.getCountry() == b2.getCountry());
		assertTrue(b1.getPlatoons().get("DF") == b2.getPlatoons().get("DF"));
		assertFalse(b1.getPlatoons().get("seals") == b2.getPlatoons().get("seals"));
	}

	@Test
	public void unsingleton_inside_fields_map() {
		Base b1 = nut.get(Base.class, "green");
		Base b2 = nut.get(Base.class, "green");
		assertFalse(b1 == b2);
		assertEquals("United States", b1.getCountry().getName());
		assertEquals("United States", b2.getCountry().getName());
		assertTrue(b1.getCountry() == b2.getCountry());
		assertTrue(b1.getPlatoons().get("DF") == b2.getPlatoons().get("DF"));
		assertFalse(b1.getPlatoons().get("seals") == b2.getPlatoons().get("seals"));
	}

	@Test
	public void test_simple_inner_object_in_field() {
		Platoon p = nut.get(Platoon.class, "SF");
		assertEquals(Base.class,p.getBase().getClass());
	}
}
