package org.nutz.aop.javassist;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.nutz.aop.javassist.meta.Vegetarian;
import org.nutz.aop.javassist.meta.Zebra;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.json.Json;

public class NutIocAopTest {

	public static int[] CC = { 0, 0, 0, 0 };

	private static void resetCC() {
		Arrays.fill(CC, 0);
	}

	@Test
	public void by_match_by_name_aop_test() {
		resetCC();
		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/aop/javassist/aop.js"));
		Zebra zebra = ioc.get(Zebra.class, "r");
		zebra.doSomething(Vegetarian.BEH.run);
		assertEquals("[2, 2, 0, 0]", Json.toJson(CC));
	}

}
