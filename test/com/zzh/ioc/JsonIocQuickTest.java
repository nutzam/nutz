package com.zzh.ioc;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zzh.dao.test.meta.Base;
import com.zzh.ioc.impl.NutIoc;

public class JsonIocQuickTest {

	@Test
	public void most_simple_json_ioc_string() {
		Ioc ioc = new NutIoc("{red:{name:'Red',level:45}}");
		Base b = ioc.get(Base.class, "red");
		assertEquals("Red", b.getName());
		assertEquals(45, b.getLevel());
	}
	
	@Test
	public void inject_by_refer$name(){
		Ioc ioc = new NutIoc("{red:{name:{refer:'@name'},level:45}}");
		Base b = ioc.get(Base.class, "red");
		assertEquals("red", b.getName());
		assertEquals(45, b.getLevel());
	}
}
