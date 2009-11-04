package org.nutz.ioc.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.nutz.dao.Sqls;
import org.nutz.dao.test.DaoCase;
import org.nutz.ioc.meta.fake.Faking;
import org.nutz.lang.Lang;

public class ObjServiceTest extends DaoCase {

	private Faking faking;
	private ObjService srv;

	@Override
	protected void before() {
		this.faking = new Faking();
		this.srv = new ObjService(dao);
		Sqls.executeDefinitionFile(dao, "org/nutz/ioc/meta/ioc.dod");
	}

	private void prepareFixData1() {
		Obj[] objs = faking.getObjs();
		for (Obj obj : objs)
			srv.insertObj(obj);
	}

	@Test
	public void testQuery() {
		prepareFixData1();
		List<Obj> objs = srv.objs().query(null, null);
		assertEquals(5, objs.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFetchObject() {
		prepareFixData1();
		Obj obj = srv.fetchObject("dataSource");
		assertEquals("This is Apache common Data Source object", obj.getComment());
		assertEquals("org.apache.commons.dbcp.BasicDataSource", obj.getType());
		Map<String, Fld> flds = (Map<String, Fld>) Lang.array2map(HashMap.class, obj.getFields(),
				"name");
		assertEquals("org.postgresql.Driver", flds.get("driverClassName").getVal().getValue());
		assertEquals("jdbc:postgresql://localhost:5432/zmole", flds.get("url").getVal().getValue());
		assertEquals("admin", flds.get("username").getVal().getValue());
		assertEquals("admin", flds.get("password").getVal().getValue());
		assertTrue(obj.isSingleton());
		assertNull(obj.getParent());
		assertEquals("close", obj.getLifecycle().getDepose());

		obj = srv.fetchObject("dao");
		assertTrue(obj.getArgs()[0].isRefer());
		assertEquals("dataSource", obj.getArgs()[0].getValue());
		assertTrue(obj.getArgs()[1].isInner());
		Obj sqls = (Obj) obj.getArgs()[1].getValue();
		assertEquals("sqls", sqls.getName());
		assertEquals("org.nutz.dao.impl.FileSqlManager", sqls.getType());
		assertTrue(sqls.isSingleton());
		assertNull(sqls.getLifecycle());
		assertEquals("sqls/zmole.sqls", sqls.getArgs()[0].getValue());
		assertEquals("keys", sqls.getFields()[0].getName());
		assertTrue(sqls.getFields()[0].getVal().isJava());
		assertEquals("org.nutz.fake.FakeKeys.keys", sqls.getFields()[0].getVal().getValue());

		obj = srv.fetchObject("colors");
		assertNull(obj.getComment());
		assertEquals("org.nutz.fake.ColorDeposer", obj.getLifecycle().getDepose());
		assertTrue(obj.getArgs()[0].isRefer());
		assertEquals("dao", obj.getArgs()[0].getValue());
		assertTrue(obj.getArgs()[1].isDisk());
		assertEquals("org.nutz/mole/Mole.class", obj.getArgs()[1].getValue());
		flds = (Map<String, Fld>) Lang.array2map(HashMap.class, obj.getFields(), "name");
		assertEquals("ZZH_OUTPUT_DIR", flds.get("output").getVal().getValue());
		assertTrue(flds.get("output").getVal().isEnv());
		assertEquals("file/index.o", flds.get("indexFile").getVal().getValue());
		assertTrue(flds.get("indexFile").getVal().isFile());

		obj = srv.fetchObject("reds");
		assertEquals("colors", obj.getParent());
		assertEquals(0, obj.getArgs().length);
		flds = (Map<String, Fld>) Lang.array2map(HashMap.class, obj.getFields(), "name");
		assertEquals(2, ((Object[]) flds.get("array").getVal().getValue()).length);
		assertEquals("A", ((Object[]) flds.get("array").getVal().getValue())[0]);
		assertEquals("B", ((Object[]) flds.get("array").getVal().getValue())[1]);
		assertEquals("jsp.main.show", flds.get("jspFile").getVal().getValue());
		assertTrue(flds.get("callback").getVal().isJava());
		assertEquals("org.nutz.Static.name", flds.get("callback").getVal().getValue());

		obj = srv.fetchObject("blues");
		assertEquals(1, obj.getArgs().length);
		assertTrue(obj.getArgs()[0].isNull());
		assertNull(obj.getArgs()[0].getValue());
		flds = (Map<String, Fld>) Lang.array2map(HashMap.class, obj.getFields(), "name");
		Val v = flds.get("setting").getVal();
		assertTrue(v.isMap());
		Map<?, ?> map = (Map<?, ?>) v.getValue();
		assertEquals(6, map.size());
		assertEquals(45, map.get("x"));
		assertEquals(78, map.get("y"));
		assertEquals(100, map.get("width"));
		assertEquals(49, map.get("height"));
		Val vf = (Val) map.get("oneFile");
		assertTrue(vf.isFile());
		assertEquals("ttt.txt", vf.getValue());
		map = (Map<?, ?>) map.get("map");
		assertEquals(10, map.get("a"));
		assertFalse((Boolean) map.get("b"));
		assertTrue(flds.get("disabled").getVal().isBoolean());
		assertTrue((Boolean) flds.get("disabled").getVal().getValue());
		assertTrue(flds.get("refer").getVal().isNull());
		assertNull(flds.get("refer").getVal().getValue());
	}

	@Test
	public void testUpdateObj() {
		prepareFixData1();
		Obj obj = srv.fetchObject("blues");
		obj.setArgs(new Val[2]);
		obj.getArgs()[0] = Val.make(Val.normal, "/abc.z");
		obj.getArgs()[1] = Val.make(Val.bool, true);
		obj.setFields(new Fld[0]);
		srv.updateObj(obj);
		obj = srv.fetchObject("blues");
		assertEquals("/abc.z", obj.getArgs()[0].getValue());
		assertTrue((Boolean) obj.getArgs()[1].getValue());
		assertEquals(0, obj.getFields().length);
	}

}
