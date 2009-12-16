package org.nutz.ioc.json;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.json.pojo.AnimalRace;
import org.nutz.ioc.loader.json.JsonLoader;

public class AdvanceJsonIocTest {

	/**
	 * Issue 12
	 */
	@Test
	public void test_import() {
		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/ioc/json/main.js"));

		Animal zzh = ioc.get(Animal.class, "zzh");
		Animal xb = ioc.get(Animal.class, "xb");
		Animal blue = ioc.get(Animal.class, "blue");

		assertEquals("Peter Zhang", zzh.getName());
		assertEquals(AnimalRace.HUMAN, zzh.getRace());
		assertEquals(1.0, zzh.getAttact(), 2);

		assertEquals("XiaoBai", xb.getName());
		assertEquals(AnimalRace.MAMMAL, xb.getRace());
		assertEquals(78.0, xb.getAttact(), 2);

		assertEquals("Blue", blue.getName());
		assertEquals(AnimalRace.MAMMAL, blue.getRace());
		assertEquals(957.0, blue.getAttact(), 2);
	}
	
	/**
	 * Issue 70
	 */
	@Test
	public void test_vars(){
		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/ioc/json/vars.js"));
		
		Animal zzh = ioc.get(Animal.class, "zzh");
		Animal xb = ioc.get(Animal.class, "xb");
		Animal blue = ioc.get(Animal.class, "blue");
		Animal red = ioc.get(Animal.class, "red");
		
		assertEquals("Red", red.getName());
		assertEquals(AnimalRace.MAMMAL, red.getRace());
		assertEquals("2009-08-08 12:23:23", Castors.me().castToString(red.getBirthday()));

		assertEquals("Peter Zhang", zzh.getName());
		assertEquals(AnimalRace.HUMAN, zzh.getRace());
		assertEquals(1.0, zzh.getAttact(), 2);

		assertEquals("XiaoBai", xb.getName());
		assertEquals(AnimalRace.MAMMAL, xb.getRace());
		assertEquals(78.0, xb.getAttact(), 2);

		assertEquals("Blue", blue.getName());
		assertEquals(AnimalRace.MAMMAL, blue.getRace());
		assertEquals(957.0, blue.getAttact(), 2);
	}

}
