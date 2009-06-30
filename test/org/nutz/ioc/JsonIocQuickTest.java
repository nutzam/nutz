package org.nutz.ioc;

import static org.junit.Assert.*;

import org.junit.Test;

import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Platoon;
import org.nutz.ioc.impl.NutIoc;

public class JsonIocQuickTest {

	@Test
	public void most_simple_json_ioc_string() {
		Ioc ioc = new NutIoc("{red:{name:'Red',level:45}}");
		Base b = ioc.get(Base.class, "red");
		assertEquals("Red", b.getName());
		assertEquals(45, b.getLevel());
	}

	@Test
	public void inject_by_refer$name() {
		Ioc ioc = new NutIoc("{red:{name:{refer:'@name'},level:45}}");
		Base b = ioc.get(Base.class, "red");
		assertEquals("red", b.getName());
		assertEquals(45, b.getLevel());
	}

	@Test
	public void inject_by_quick_fields() {
		Ioc ioc = new NutIoc("{b:{name:'Red',countryId:23,level:4}}");
		Base b = ioc.get(Base.class, "b");
		assertEquals("Red", b.getName());
		assertEquals(23, b.getCountryId());
		assertEquals(4, b.getLevel());
	}

	@Test
	public void inject_by_quick_inner_fields() {
		Ioc ioc = new NutIoc("{p:{base:{name:'Red'}}}");
		Platoon p = ioc.get(Platoon.class, "p");
		assertEquals("Red", p.getBase().getName());
	}

	@Test
	public void inject_by_quick_inner_coll_fields() {
		Ioc ioc = new NutIoc("{p:{soliders:[{type:'org.nutz.dao.test.meta.Soldier',fields:{name:'zzh'}}]}}");
		Platoon p = ioc.get(Platoon.class, "p");
		assertEquals("zzh", p.getSoliders().get(0).getName());
	}
}
