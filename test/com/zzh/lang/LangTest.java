package com.zzh.lang;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.zzh.castor.FailToCastObjectException;
import com.zzh.json.Json;

public class LangTest {

	public static class A {
		private int id;
		private String name;
	}

	@Test
	public void testMap2Object() throws FailToCastObjectException {
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(Lang.inr("{id:23,name:'zzh'}"));
		A a = Lang.map2Object(map, A.class);
		assertEquals(23, a.id);
		assertEquals("zzh", a.name);
	}

}
