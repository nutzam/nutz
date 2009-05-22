package com.zzh.lang;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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

	@Test
	public void test_char_reader() throws IOException {
		Reader r = Lang.inr("AB");
		assertEquals('A', (char) r.read());
		assertEquals('B', (char) r.read());
		assertEquals(-1, r.read());
	}

	@Test
	public void test_char_writer() throws IOException {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		w.write("AB");
		assertEquals("AB", sb.toString());
	}

}
