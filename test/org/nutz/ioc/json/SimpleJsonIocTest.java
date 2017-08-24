package org.nutz.ioc.json;

import static org.junit.Assert.*;
import static org.nutz.ioc.json.Utils.A;
import static org.nutz.ioc.json.Utils.I;
import static org.nutz.ioc.json.Utils.J;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.json.pojo.AnimalRace;
import org.nutz.ioc.json.pojo.IocSelf;
import org.nutz.ioc.json.pojo.IocTO00;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutMap;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class SimpleJsonIocTest {

    @Test
    public void test_2darray_by_map_iocvalue() {
        Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

        Map<String, Object> objMap = new HashMap<String, Object>();
        String[][] strss = new String[2][2];
        strss[0][0] = "a";
        strss[0][1] = "b";
        strss[1][0] = "c";
        strss[1][1] = "d";
        objMap.put("args", new Object[]{strss});

        map.put("obj", objMap);
        Ioc ioc = new NutIoc(new MapLoader(map));

        IocTO00 obj = ioc.get(IocTO00.class, "obj");
        assertEquals(2, obj.getStrss().length);
        assertEquals(2, obj.getStrss()[0].length);
        assertEquals("a", obj.getStrss()[0][0]);
        assertEquals("b", obj.getStrss()[0][1]);
        assertEquals("c", obj.getStrss()[1][0]);
        assertEquals("d", obj.getStrss()[1][1]);
    	ioc.depose();
    }

    @Test
    public void test_2darray_iocvalue() {
        Ioc ioc = I("obj:{args:[[['a','b'],['c','d']]]}");
        IocTO00 obj = ioc.get(IocTO00.class, "obj");
        assertEquals(2, obj.getStrss().length);
        assertEquals(2, obj.getStrss()[0].length);
        assertEquals("a", obj.getStrss()[0][0]);
        assertEquals("b", obj.getStrss()[0][1]);
        assertEquals("c", obj.getStrss()[1][0]);
        assertEquals("d", obj.getStrss()[1][1]);
    }

    @Test
    public void test_refer_self() {
        Ioc ioc = I(J("fox", "name:'Fox',another:{refer:'fox'}"));
        Animal f = ioc.get(Animal.class, "fox");
        assertEquals("Fox", f.getName());
        assertTrue(f == f.getAnother());
    }

    @Test
    public void test_null_json_file() {
        IocLoader loader = new JsonLoader("org/nutz/ioc/json/empty.js");
        Ioc ioc = new NutIoc(loader);
        assertEquals(0, ioc.getNames().length);
    	ioc.depose();
    }

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
        Ioc ioc = I(J("fox", "type:'org.nutz.ioc.json.pojo.Animal',fields:{name:'Fox'}"),
                    J("rabit", "name:'Rabit',enemies:[{refer:'fox'},{refer:'fox'}]"));
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
        Ioc ioc = I(J("fox", "name:'Fox'"),
                    J(    "rabit",
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
        Animal f = A("name:{env:'PATH'},misc:[{env:'PATH'}]");
        assertTrue(f.getName().length() > 0);
        assertEquals(f.getName(), f.getMisc().get(0).toString());
    }

    @Test
    public void test_sys() {
        Properties properties = System.getProperties();
        properties.put("sysA", "XX");
        properties.put("sysP", "ZZZ");
        Animal f = A("name:{sys:'sysA'},misc:[{sys:'sysP'}]");
        assertEquals("XX", f.getName());
        assertEquals("ZZZ", f.getMisc().get(0).toString());
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
        Ioc ioc = I(J("fox", "name:'Fox',age:10"),
                    J("wolf", "name:{java:'$fox.showName(\"_\", 2, \"W\")'},age:{java:'$fox.age'}"));
        Animal fox = ioc.get(Animal.class, "fox");
        Animal wolf = ioc.get(Animal.class, "wolf");
        assertEquals("Fox", fox.getName());
        assertEquals(fox.getAge(), wolf.getAge());
        assertEquals("__W", wolf.getName());
    }

    @Test
    public void test_parent() {
        Ioc ioc = I(J("fox", "name:'P',age:10"), J("f2", "parent:'fox',fields:{age:5}"));
        Animal fox = ioc.get(Animal.class, "fox");
        assertEquals("P", fox.getName());
        assertEquals(10, fox.getAge());
        Animal f2 = ioc.get(Animal.class, "f2");
        assertEquals("P", f2.getName());
        assertEquals(5, f2.getAge());
    }

    @Test
    public void test_muilt_parent() {
        Ioc ioc = I(J("fox", "name:'P',age:10"), J("f2", "parent:'fox'"), J("f3", "parent:'f2'"));
        Animal f3 = ioc.get(Animal.class, "f3");
        assertEquals(10, f3.getAge());
    }

    @Test
    public void test_create_by_args() {
        Ioc ioc = I(J("fox", "age:10"), J("xb", "parent:'fox',args:['XiaoBai']"));
        Animal xb = ioc.get(Animal.class, "xb");
        assertEquals("XiaoBai", xb.getName());
    }

    @Test(expected = IocException.class)
    public void test_break_parent() {
        Ioc ioc = I(J("f2", "parent:'f3'"), J("f3", "parent:'f2'"));
        ioc.get(Animal.class, "f3");
    }

    @Test(expected = IocException.class)
    public void test_break_parent2() {
        Ioc ioc = I(J("fox", "name:'P',age:10"), J("f2", "parent:'x'"), J("f3", "parent:'y'"));
        ioc.get(Animal.class, "f3");
    }

    @Test
    public void test_load_from_dir() throws ObjectLoadException {
        IocLoader loader = new JsonLoader("org/nutz/ioc/json/");
        assertTrue(loader.getName().length > 0);
    }

    @Test
    public void test_load_from_reader() throws ObjectLoadException {
        IocLoader loader = new JsonLoader(Streams.fileInr("org/nutz/ioc/json/main.js"));
        assertTrue(loader.getName().length > 0);
    }

    @Test
    public void test_get_ioc_self() {
        Ioc ioc = I(J("iocV", "type:'org.nutz.ioc.json.pojo.IocSelf',fields:{ioc:{refer : '$iOc'}}"));
        assertEquals(ioc, ioc.get(IocSelf.class, "iocV").getIoc());
    }
    
    @Test
    public void test_env_list() {
        Animal f = A("name:{env:['java.tmp.file', '/wendal']},misc:[{env:['!PATH', '/' , 'os.name', '/zozoh']}]");
        assertTrue(f.getName().length() > 0);
        assertTrue(f.getName().contains("/wendal"));
        assertTrue(f.getMisc().get(0).toString().endsWith("/zozoh"));
        System.out.println(f.getName());
        System.out.println(f.getMisc().get(0));
        
        f = A("name:{env:['!JAVA_HOME:/opt/jdk6', '/bin/java']}");
        assertTrue(f.getName().contains("/bin/java"));
        assertTrue(f.getName().length() > "/bin/java".length());
        
        f = A("name:{env:['!ERR_JAVA_HOME:/opt/jdk6', '/bin/java']}");
        assertTrue(f.getName().contains("/bin/java"));
        assertTrue(f.getName().length() > "/bin/java".length());
        assertEquals("/opt/jdk6/bin/java", f.getName());
    }
    
    @Test
    public void test_sys_list() {
        Animal f = A("name:{sys:['/tmp/','PATH', '/wendal']},misc:[{sys:['PATH', '/' , 'os.name', '/zozoh']}]");
        assertTrue(f.getName().length() > 0);
        assertTrue(f.getName().contains("/wendal"));
        assertTrue(f.getName().contains("/tmp"));
        assertTrue(f.getMisc().get(0).toString().endsWith("/zozoh"));
    }
    
    @Test
    public void test_json_format_itself() {
        Json.toJson(JsonFormat.full());
    }
    
    @Test
    public void test_java_neg() {
        Animal f = A("name:{java:'org.nutz.ioc.json.pojo.JavaValueTest.abc(\"/tmp/\", -1)'}");
        assertTrue(f.getName().length() > 0);
        System.out.println(f.getName());
        assertTrue(f.getName().equals("/tmp/,-1"));
    }
    
    @Test
    public void test_nan() {
        NutMap map = new NutMap();
        map.put("key", Double.NaN);
        assertEquals("{\"key\":null}", Json.toJson(map, JsonFormat.tidy()));
    }
    
    @Test
    public void test_factory_by_factory() {
        List<NutResource> res = Scans.me().scan("org/nutz/ioc/json/issue1304/");
        System.out.println(res.get(0).getClass());
        JsonLoader loader = new JsonLoader("org/nutz/ioc/json/issue1304/");
        Ioc ioc = new NutIoc(loader);
        ioc.get(null, "c");
        ioc.depose();
    }
}
