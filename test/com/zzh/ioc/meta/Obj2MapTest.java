package com.zzh.ioc.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.util.Map;

import org.junit.Test;

import com.zzh.json.Json;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;

public class Obj2MapTest {
	@Test
	public void testRender() throws Exception {
		FileReader reader = new FileReader(Files.findFile("com/zzh/ioc/meta/map.js"));
		String s1 = Lang.readAll(reader);
		Map<?, ?> map = (Map<?, ?>) Json.fromJson(s1);
		s1 = Json.toJson(map);
		Obj obj = Map2Obj.parse(map);
		Map<?, ?> map2 = Obj2Map.render(obj);
		String s2 = Json.toJson(map2);
		assertTrue(Lang.equals(map, map2));
		assertEquals(s1, s2);
	}
}
