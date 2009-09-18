package org.nutz.dao.tools.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.nutz.dao.tools.DField;
import org.nutz.dao.tools.DTable;
import org.nutz.dao.tools.DTableParser;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class NutDTableParserTest {

	private static DTable FT(String s) {
		DTableParser parser = new NutDTableParser();
		return parser.parse(s).get(0);
	}

	@Test
	public void test_number() {
		DTable dt = FT("pet{n NUMERIC(15,10) !+}");
		assertEquals(1, dt.getFields().size());
		DField df = dt.getFields().get(0);
		assertEquals("n", df.getName());
		assertEquals("NUMERIC(15,10)", df.getType());
		assertTrue(df.isNotNull());
		assertTrue(df.isAutoIncreament());
		assertFalse(df.isPrimaryKey());
		assertFalse(df.isUnique());
		assertFalse(df.isUnsign());
	}

	@Test
	public void test_simple_string() {
		DTable dt = FT("pet{id INT +PK, name VARCHAR(20) !UNIQUE ," + " age INT <30> , color CHAR(10) ! <'red'>}");
		List<DField> fields = dt.getFields();
		assertEquals(4, fields.size());
		// id
		DField df = fields.get(0);
		assertEquals("id", df.getName());
		assertEquals("INT", df.getType());
		assertNull(df.getDefaultValue());
		assertTrue(df.isPrimaryKey());
		assertTrue(df.isUnique());
		assertTrue(df.isAutoIncreament());
		assertTrue(df.isNotNull());
		assertFalse(df.isUnsign());
		// name
		df = fields.get(1);
		assertEquals("name", df.getName());
		assertEquals("VARCHAR(20)", df.getType());
		assertNull(df.getDefaultValue());
		assertFalse(df.isPrimaryKey());
		assertTrue(df.isUnique());
		assertFalse(df.isAutoIncreament());
		assertTrue(df.isNotNull());
		assertFalse(df.isUnsign());
		// age
		df = fields.get(2);
		assertEquals("age", df.getName());
		assertEquals("INT", df.getType());
		assertEquals("30", df.getDefaultValue());
		assertFalse(df.isPrimaryKey());
		assertFalse(df.isUnique());
		assertFalse(df.isAutoIncreament());
		assertFalse(df.isNotNull());
		assertFalse(df.isUnsign());
		// color
		df = fields.get(3);
		assertEquals("color", df.getName());
		assertEquals("CHAR(10)", df.getType());
		assertEquals("'red'", df.getDefaultValue());
		assertFalse(df.isPrimaryKey());
		assertFalse(df.isUnique());
		assertFalse(df.isAutoIncreament());
		assertTrue(df.isNotNull());
		assertFalse(df.isUnsign());
	}

	@Test
	public void test_file_1() {
		String path = "org/nutz/dao/tools/impl/test1.txt";
		String s = Lang.readAll(Streams.fileInr(path));
		DTableParser parser = new NutDTableParser();
		List<DTable> dts = parser.parse(s);
		assertEquals(4, dts.size());
		DTable dt;
		// t_food
		dt = dts.get(2);
		assertTrue(dt.getField("id").isPrimaryKey());
		assertEquals("'BANANA'", dt.getField("name").getDefaultValue());
		// t_pet_food
		dt = dts.get(3);
		assertEquals("INT", dt.getField("petId").getType());
	}

	@Test
	public void test_one_static_field() {
		DTable dt = FT("nut_lifecycle {id INT PK,  born VARCHAR(100),  depose VARCHAR(100),  fetch VARCHAR(100)}");
		DField df = dt.getField("id");
		assertEquals(0, dt.getAutoIncreaments().size());
		assertEquals(1, dt.getPks().size());
		assertTrue(df.isPrimaryKey());
		assertFalse(df.isAutoIncreament());
		assertFalse(df.isUnsign());
		assertTrue(df.isNotNull());
		assertTrue(df.isUnique());
	}

}
