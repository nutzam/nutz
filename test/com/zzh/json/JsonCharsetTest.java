package com.zzh.json;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.junit.Test;

import com.zzh.lang.Files;

@SuppressWarnings("unchecked")
public class JsonCharsetTest {

	@Test
	public void test_simple_map() {
		String str = "{name:\"张志昊\"}";
		Map<String, String> map = (Map<String, String>) Json.fromJson(str);
		String json = Json.toJson(map, JsonFormat.compact().setQuoteName(false));
		assertEquals(str, json);
	}

	@Test
	public void test_zh_CN_from_file() throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream(Files
				.findFile("com/zzh/json/zh_CN.txt")));
		Map<String,String> map = (Map<String,String>)Json.fromJson(reader);
		assertEquals("张",map.get("name"));
	}

}
