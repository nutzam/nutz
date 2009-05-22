package com.zzh.ioc.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.util.Map;

import org.junit.Test;

import com.zzh.json.Json;
import com.zzh.lang.Files;
import com.zzh.lang.meta.Email;
import com.zzh.ioc.meta.fake.FakeDeposer;
import com.zzh.ioc.meta.fake.FakeInnerObject;
import com.zzh.ioc.meta.fake.FakeObject;

public class Map2ObjTest {

	@Test
	public void testParse() throws Exception {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(new FileReader(Files
				.findFile("com/zzh/ioc/meta/map.js")));
		Obj obj = Map2Obj.parse(map);
		/*
		 * Basic setting
		 */
		assertEquals(45, obj.getId());
		assertFalse(obj.isSingleton());
		assertEquals("Hello Peter", obj.getComment());
		assertEquals(FakeObject.class.getName(), obj.getType());
		assertEquals("testObj", obj.getName());
		assertEquals("fakeParent", obj.getParent());
		assertEquals(FakeDeposer.class.getName(), obj.getDeposer());
		assertEquals("close", obj.getDeposeby());
		/*
		 * Arguments
		 */
		assertEquals("arg0", obj.getArgs()[0].getValue());
		assertTrue(obj.getArgs()[1].isConfig());
		assertEquals("someParam", obj.getArgs()[1].getValue());
		// For inner
		Obj inner = (Obj) obj.getArgs()[2].getValue();
		assertTrue(inner.isSingleton());
		assertNull(inner.getComment());
		assertEquals("testInner", inner.getName());
		assertEquals(FakeInnerObject.class.getName(), inner.getType());
		assertNull(inner.getParent());
		assertNull(inner.getDeposeby());
		assertNull(inner.getDeposer());
		assertEquals("a", inner.getArgs()[0].getValue());
		assertTrue((Boolean) inner.getArgs()[1].getValue());
		assertTrue(inner.getArgs()[2].isEnv());
		assertEquals("TOMCAT_HOME", inner.getArgs()[2].getValue());
		assertEquals("A", inner.getFields()[0].getName());
		assertEquals("AA", inner.getFields()[0].getVal().getValue());
		// end inner
		/*
		 * Fields
		 */
		assertEquals("A", obj.getFields()[0].getName());
		assertEquals("a", obj.getFields()[0].getVal().getValue());
		assertEquals("B", obj.getFields()[1].getName());
		assertTrue(obj.getFields()[1].getVal().isFile());
		assertEquals("/WEB-INF/web.xml", obj.getFields()[1].getVal().getValue());
		// for array
		assertEquals("C", obj.getFields()[2].getName());
		assertTrue(obj.getFields()[2].getVal().isArray());
		Object[] objs = (Object[]) obj.getFields()[2].getVal().getValue();
		assertEquals("c0", (String) objs[0]);
		assertNull(objs[1]);
		assertTrue(((Val) objs[2]).isJava());
		assertEquals("com.zzh.ioc.meta.fake.Fake.id", ((Val) objs[2]).getValue());
		assertEquals(59, (Integer) objs[3]);
		// for map
		assertEquals("D", obj.getFields()[3].getName());
		assertTrue(obj.getFields()[3].getVal().isMap());
		Map<?, ?> dm = (Map<?, ?>) obj.getFields()[3].getVal().getValue();
		assertEquals(105, dm.get("M1"));
		assertTrue(((Val) dm.get("M2")).isServer());
		assertEquals("attName", ((Val) dm.get("M2")).getValue());
	}

	@Test
	public void parse_with_null_value_field() {
		Map<?, ?> map = (Map<?, ?>) Json
				.fromJson("{name:'O',type:null,singleton:true,fields:{A:null}}");
		Obj obj = Map2Obj.parse(map);
		assertEquals(1, obj.getFields().length);
		assertEquals("A", obj.getFields()[0].getName());
		assertTrue(obj.getFields()[0].getVal().isNull());
		assertNull(obj.getFields()[0].getVal().getValue());
	}

	@Test
	public void parse_with_directly_fields() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson("{A:12,B:[true,8.67]}");
		Obj obj = Map2Obj.parse(map);
		assertTrue(obj.isSingleton());
		assertNull(obj.getName());
		assertNull(obj.getType());
		assertEquals(2, obj.getFields().length);
		assertEquals("A", obj.getFields()[0].getName());
		assertTrue(obj.getFields()[0].getVal().isNormal());
		assertEquals(12, obj.getFields()[0].getVal().getValue());
		assertEquals("B", obj.getFields()[1].getName());
		assertTrue(obj.getFields()[1].getVal().isArray());
		Object[] vs = (Object[]) obj.getFields()[1].getVal().getValue();
		assertEquals(2, vs.length);
		assertTrue((Boolean) vs[0]);
		assertEquals(8.67, vs[1]);
	}

	@Test
	public void parse_with_simple_inner_object() {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson("{A:{type:\"innerType\"}}");
		Obj obj = Map2Obj.parse(map);
		assertNull(obj.getType());
		assertTrue(obj.getFields()[0].getVal().isInner());
	}

	@Test
	public void parse_with_inner_object_in_array() {
		Map<?, ?> map = (Map<?, ?>) Json
				.fromJson("{A:[{type:'com.zzh.lang.meta.Email',args:['abc@x.com']}]}");
		Obj obj = Map2Obj.parse(map);
		assertEquals("A", obj.getFields()[0].getName());
		Object[] vs = (Object[]) obj.getFields()[0].getVal().getValue();
		Obj inner = (Obj) ((Val) vs[0]).getValue();
		assertEquals(Email.class.getName(), inner.getType());
		assertEquals("abc@x.com", inner.getArgs()[0].getValue());
	}

}
