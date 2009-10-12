package org.nutz.mvc.init;

import static org.junit.Assert.*;

import org.junit.Test;

public class PathNodeTest {

	@Test
	public void simple_matching(){
		PathNode<String> node = new PathNode<String>();
		node.add("a*", "A");
		node.add("b01", "B");
		node.add("b02", "C");
		
		assertEquals("A",node.get("acd"));
		assertEquals("A",node.get("a"));
		assertEquals("B",node.get("b01"));
		assertEquals("C",node.get("b02"));
		assertNull(node.get("b0"));
		assertNull(node.get("FFF"));
	}
	
	@Test
	public void try_not_found(){
		PathNode<String> node = new PathNode<String>();
		node.add("ab", "A");
		
		assertEquals("A",node.get("ab"));
		assertNull(node.get("abc"));
		
		node = new PathNode<String>();
		node.add("a", "A");
		
		assertEquals("A",node.get("a"));
		assertNull(node.get("abc"));
	}
}
