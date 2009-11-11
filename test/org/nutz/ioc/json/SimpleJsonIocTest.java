package org.nutz.ioc.json;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.json.pojo.AnimalRace;

import static org.nutz.ioc.json.Utils.*;

public class SimpleJsonIocTest {

	@Test
	public void test_normal() {
		Animal a = A("age:23,name:'monkey',race:'MAMMAL'");
		assertEquals(23, a.getAge());
		assertEquals("monkey", a.getName());
		assertEquals(AnimalRace.MAMMAL, a.getRace());
	}

	@Test
	public void test_singleon() {
		Ioc ioc = I(J("fox", "name:'Fox'"));
		Animal f = ioc.get(Animal.class, "fox");
		Animal f2 = ioc.get(Animal.class, "fox");
		assertTrue(f == f2);

		ioc = I(J("fox", "singleton:false, fields: {name:'Fox'}"));
		Animal f3 = ioc.get(Animal.class, "fox");
		Animal f4 = ioc.get(Animal.class, "fox");
		assertFalse(f3 == f4);
	}

	@Test
	public void test_refer() {
		Ioc ioc = I(J("fox", "type:'org.nutz.ioc.json.pojo.Animal',fields:{name:'Fox'}"), J(
				"rabit", "name:'Rabit',enemies:[{refer:'fox'},{refer:'fox'}]"));
		Animal r = ioc.get(Animal.class, "rabit");
		Animal f = ioc.get(Animal.class, "fox");
		assertEquals(2, r.getEnemies().length);
		assertTrue(f == r.getEnemies()[0]);
		assertTrue(f == r.getEnemies()[1]);
		assertEquals("Fox", f.getName());
		assertEquals("Rabit", r.getName());
	}

	@Test
	public void test_array_and_refer() {
		Ioc ioc = I(J("fox", "name:'Fox'"), J("rabit",
				"name:'Rabit',enemies:[{refer:'fox:org.nutz.ioc.json.pojo.Animal'},null]"));

		Animal r = ioc.get(Animal.class, "rabit");
		Animal f = ioc.get(Animal.class, "fox");
		assertEquals(2, r.getEnemies().length);
		assertTrue(f == r.getEnemies()[0]);
		assertEquals("Fox", f.getName());
		assertEquals("Rabit", r.getName());
		assertNull(r.getEnemies()[1]);
	}

	@Test
	public void test_env() {
		Animal f = A("name:{env:'path'},misc:[{env:'path'}]");
		assertTrue(f.getName().length() > 0);
		assertEquals(f.getName(), f.getMisc().get(0).toString());
	}

	@Test
	public void test_file() {
		Animal f = A("misc:[{file:'org/nutz/ioc/json/pojo/Animal.class'}]");
		assertEquals("Animal.class", ((File) f.getMisc().get(0)).getName());
	}

	@Test
	public void test_inner() {
		Animal f = A("enemies: [ {type:'org.nutz.ioc.json.pojo.Animal', fields: {name:'xxx'}} ]");
		assertEquals("xxx", f.getEnemies()[0].getName());
	}

	@Test
	public void test_map() {
		Animal f = A("map : {asia:34, europe: 45}");
		assertEquals(34, f.getMap().get("asia").intValue());
		assertEquals(45, f.getMap().get("europe").intValue());

		f = A("relations: {a: {type:'org.nutz.ioc.json.pojo.Animal', fields: {name:'AAA'}}"
				+ ",b: {type:'org.nutz.ioc.json.pojo.Animal', fields: {name:'BBB'}}}");
		assertEquals(2, f.getRelations().size());
		assertEquals("AAA", f.getRelations().get("a").getName());
		assertEquals("BBB", f.getRelations().get("b").getName());
	}

	@Test
	public void test_java_simple() {
		Ioc ioc = I(J("fox", "name:{java: '@Name.toUpperCase()'}, age:{java:'@Name.length()'}"));
		Animal fox = ioc.get(Animal.class, "fox");
		assertEquals("FOX", fox.getName());
		assertEquals(3, fox.getAge());
	}

	@Test
	public void test_java_with_arguments() {
		Ioc ioc = I(J("fox", "name:'Fox',age:10"), J("wolf",
				"name:{java:'$fox.showName(\"_\", 2, \"W\")'},age:{java:'$fox.age'}"));
		Animal fox = ioc.get(Animal.class, "fox");
		Animal wolf = ioc.get(Animal.class, "wolf");
		assertEquals("Fox", fox.getName());
		assertEquals(fox.getAge(), wolf.getAge());
		assertEquals("__W", wolf.getName());
	}

}
