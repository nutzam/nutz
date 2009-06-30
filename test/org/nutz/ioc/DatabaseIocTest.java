package org.nutz.ioc;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import org.nutz.dao.test.DaoCase;
import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.ioc.db.DatabaseLoader;
import org.nutz.ioc.meta.Map2Obj;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.ObjService;
import org.nutz.json.Json;
import org.nutz.ioc.impl.NutIoc;

public class DatabaseIocTest extends DaoCase {

	private Ioc ioc;
	private ObjService srv;

	@Override
	protected void before() {
		pojos.execFile("org/nutz/ioc/dbtest.sqls");
		ioc = new NutIoc(new DatabaseLoader(dao));
		srv = new ObjService(dao);
	}

	@Override
	protected void after() {
		ioc.depose();
	}

	@Test
	public void simple_inject_platoon() {
		String str = "{id:34,name:'ttt',baseName:'red',leaderName:'zzh'}";
		Obj obj = Map2Obj.parse((Map<?, ?>) Json.fromJson(str));
		obj.setName("p1");
		srv.insertObj(obj);
		Platoon p = ioc.get(Platoon.class, "p1");
		assertEquals(34, p.getId());
		assertEquals("ttt", p.getName());
		assertEquals("red", p.getBaseName());
		assertEquals("zzh", p.getLeaderName());
	}

	@Test
	public void simple_inner_object_in_field() {
		String str = "{id:12,name:'p1',leaderName:'zzh',base : {type :\"org/nutz.dao.test.meta.Base\"}}";
		Obj obj = Map2Obj.parse((Map<?, ?>) Json.fromJson(str));
		obj.setName("p1");
		srv.insertObj(obj);
		Platoon p = ioc.get(Platoon.class, "p1");
		assertEquals(Base.class, p.getBase().getClass());
	}

}
