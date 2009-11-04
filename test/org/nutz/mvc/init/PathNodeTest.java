package org.nutz.mvc.init;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathNodeTest {

	@Test
	public void simple_matching() {
		PathNode<String> node = new PathNode<String>();
		node.add("a*", "A");
		node.add("b01", "B");
		node.add("b02", "C");

		assertEquals("A", node.get("acd").getObj());
		assertEquals("A", node.get("a").getObj());
		assertEquals("B", node.get("b01").getObj());
		assertEquals("C", node.get("b02").getObj());
		assertNull(node.get("b0").getObj());
		assertNull(node.get("FFF").getObj());
	}

	@Test
	public void try_not_found() {
		PathNode<String> node = new PathNode<String>();
		node.add("ab", "A");

		assertEquals("A", node.get("ab").getObj());
		assertNull(node.get("abc").getObj());

		node = new PathNode<String>();
		node.add("a", "A");

		assertEquals("A", node.get("a").getObj());
		assertNull(node.get("abc").getObj());
	}

	@Test
	public void test_path_args() {
		PathNode<String> node = new PathNode<String>();
		node.add("/a/*", "X");
		node.add("/b", "Y");
		node.add("/c/d*", "Z");

		PathInfo<String> info = node.get("/a/123");
		assertEquals("X", info.getObj());
		assertEquals(3, info.getCursor());
		assertEquals("123", info.getRemain());

		info = node.get("/a/3/t");
		assertEquals("X", info.getObj());
		assertEquals(3, info.getCursor());
		assertEquals("3/t", info.getRemain());

		info = node.get("/b/c");
		assertNull(info.getObj());
		assertEquals(0, info.getCursor());
		assertEquals("/b/c", info.getRemain());

		info = node.get("/b");
		assertEquals("Y", info.getObj());
		assertEquals(2, info.getCursor());
		assertNull(info.getRemain());
		
		info = node.get("/c/d/123");
		assertEquals("Z", info.getObj());
		assertEquals(4, info.getCursor());
		assertEquals("/123", info.getRemain());
		
		info = node.get("/c/d");
		assertEquals("Z", info.getObj());
		assertEquals(4, info.getCursor());
		assertEquals(null, info.getRemain());
	}
}
