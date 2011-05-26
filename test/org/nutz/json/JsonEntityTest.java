package org.nutz.json;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.json.meta.JENObj;

public class JsonEntityTest {

	@Test
	public void test_simple_JENObj() {
		String str = "{id:9999999999, name:'abc', age:10}";
		JENObj obj = Json.fromJson(JENObj.class, str);

		assertEquals(9999999999L, obj.getObjId());
		assertEquals("abc", obj.getName());
		assertEquals(10, obj.getAge());

		str = Json.toJson(obj);

		obj = Json.fromJson(JENObj.class, str);

		assertEquals(9999999999L, obj.getObjId());
		assertEquals("abc", obj.getName());
		assertEquals(10, obj.getAge());
	}

}
