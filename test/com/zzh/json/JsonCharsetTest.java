package com.zzh.json;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

@SuppressWarnings("unchecked")
public class JsonCharsetTest {

	@Test
	public void test_simple_map() {
		String str = "{name:\"张志昊\"}";
		Map<String, String> map = (Map<String, String>) Json.fromJson(str);
		String json = Json.toJson(map,JsonFormat.compact().setQuoteName(false));
		assertEquals(str, json);
	}

}
