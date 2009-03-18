package com.zzh.lang;

import java.util.Map;

import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;

import junit.framework.TestCase;

public class LangTest extends TestCase {

	public static class A{
		private int id;
		private String name;
	} 
	
	public void testMap2Object() throws FailToCastObjectException {
		Map<?,?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{id:23,name:'zzh'}"));
		A a = Lang.map2Object(map, A.class);
		assertEquals(23,a.id);
		assertEquals("zzh",a.name);
	}

}
