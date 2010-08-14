package org.nutz.yaml.impl.simple;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import org.nutz.yaml.impl.simple.YamlParsing;

public class YamlParsingTest {
	
	private StringBuilder sb;
	
	@Before
	public void init() {
		sb = new StringBuilder();
		System.out.println("=====================================");
	}

	@Test
	public void testParse() throws Throwable {
		sb.append("- name: Nutz").append("\n");
		sb.append("- 1: Nutz").append("\n");
		sb.append("- 2: Nutz").append("\n");
		sb.append("- 3: Nutz").append("\n");
		sb.append("- 4: Nutz").append("\n");
		sb.append("  x: Nutz").append("\n");
		sb.append("- 5: Nutz").append("\n");
		sb.append("  6: Nutz").append("\n");
		sb.append("  7: Nutz").append("\n");
		sb.append("  8: Nutz").append("\n");
		System.out.println("yuml = \n" + sb.toString());
		System.out.println("---------------------------------");
		System.out.println("java print = \n"+from());
	}
	
	@Test
	public void testParse2() throws Throwable {
		sb.append("name: Nutz").append("\n");
		sb.append("1: Nutz").append("\n");
		sb.append("2: Nutz").append("\n");
		sb.append("3: Nutz").append("\n");
		sb.append("4: Nutz").append("\n");
		sb.append("x: Nutz").append("\n");
		sb.append("5: Nutz").append("\n");
		sb.append("6: Nutz").append("\n");
		sb.append("7: Nutz").append("\n");
		sb.append("8: ").append("\n");
		sb.append("  - a: PP").append("\n");
		sb.append("    b: CC").append("\n");
		System.out.println(sb.toString());
		System.out.println("---------------------------------");
		System.out.println("java print = \n"+from());
	}
	
	@Test
	public void testParse3() throws Throwable {
		sb.append("name: Nutz").append("\n");
		sb.append("1: Nutz").append("\n");
		sb.append("2: Nutz").append("\n");
		sb.append("8: ").append("\n");
		sb.append("  - a: PP").append("\n");
		sb.append("    b: ").append("\n");
		sb.append("      - Kill me").append("\n");
		sb.append("      - Kill X").append("\n");
		sb.append("      - type : Wendal").append("\n");
		sb.append("      - op: delete").append("\n");
		sb.append("  - Kill SS").append("\n");
		sb.append("  - type : YY").append("\n");
		sb.append("  - op: OP").append("\n");
		sb.append("p: I am String          ").append("\n");
		System.out.println(sb.toString());
		System.out.println("---------------------------------");
		System.out.println("java print = \n"+from());
		assertTrue(true);
	}
	
	@Test
	public void testParse4() throws Throwable {
		sb.append("8: ").append("\n");
		sb.append("  - a: PP").append("\n");
		sb.append("    b: ").append("\n");
		sb.append("        - Kill X").append("\n");
		sb.append("        - type : Wendal").append("\n");
		sb.append("        - op: delete").append("\n");
		System.out.println(sb.toString());
		System.out.println("---------------------------------");
		System.out.println("java print = \n"+from());
		assertTrue(true);
	}

	public Object from() throws Throwable {
		return new YamlParsing().parse(new StringReader(sb.toString()));
	}
}
