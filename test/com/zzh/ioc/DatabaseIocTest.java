package com.zzh.ioc;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.zzh.dao.test.DaoCase;
import com.zzh.dao.test.meta.Base;
import com.zzh.dao.test.meta.Platoon;
import com.zzh.ioc.db.DatabaseMappingLoader;
import com.zzh.ioc.meta.Map2Obj;
import com.zzh.ioc.meta.Obj;
import com.zzh.ioc.meta.ObjService;
import com.zzh.json.Json;

public class DatabaseIocTest extends DaoCase {

	private Ioc ioc;
	private ObjService srv;

	@Override
	protected void before() {
		pojos.execFile("com/zzh/ioc/dbtest.sqls");
		ioc = new Nut(new DatabaseMappingLoader(dao));
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
		String str = "{id:12,name:'p1',leaderName:'zzh',base : {type :\"com.zzh.dao.test.meta.Base\"}}";
		Obj obj = Map2Obj.parse((Map<?, ?>) Json.fromJson(str));
		obj.setName("p1");
		srv.insertObj(obj);
		Platoon p = ioc.get(Platoon.class, "p1");
		assertEquals(Base.class, p.getBase().getClass());
	}

}
