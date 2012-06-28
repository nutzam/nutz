package org.nutz.ioc.json;

import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.loader.json.JsonLoader;

import static org.junit.Assert.*;
import static org.nutz.ioc.json.Utils.*;

public class EvensJsonIocTest {

    @Test
    public void test_init_with_field() {
        String s = "fields: {name:'Fox'},";
        s = s + "\nevents:{";
        s = s + "\n    create: 'org.nutz.ioc.json.pojo.WhenCreateFox'";
        s = s + "\n}";
        Ioc ioc = I(J("fox", s));

        Animal fox = ioc.get(Animal.class, "fox");
        assertEquals("$Fox", fox.getName());
    }

    @Test
    public void test_events_for_singleton() {
        String s = "fields: {name:'Fox'},";
        s = s + "\nevents:{";
        s = s + "\n    fetch: 'onFetch',";
        s = s + "\n    create: 'onCreate',";
        s = s + "\n    depose: 'onDepose'";
        s = s + "\n}";
        Ioc ioc = I(J("fox", s));

        Animal f = ioc.get(Animal.class, "fox");
        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.get(Animal.class, "fox");

        assertEquals(1, f.getCreateTime());
        assertEquals(2, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.reset();
        assertEquals(1, f.getCreateTime());
        assertEquals(2, f.getFetchTime());
        assertEquals(1, f.getDeposeTime());
    }

    @Test
    public void test_events_for_un_singleton() {
        String s = "singleton:false, fields: {name:'Fox'},";
        s = s + "\nevents:{";
        s = s + "\n    fetch: 'onFetch',";
        s = s + "\n    create: 'onCreate',";
        s = s + "\n    depose: 'onDepose'";
        s = s + "\n}";
        Ioc ioc = I(J("fox", s));

        Animal f = ioc.get(Animal.class, "fox");
        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.get(Animal.class, "fox");

        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.reset();
        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());
    }

    @Test
    public void test_events_by_trigger_for_singleton() {
        String s = "fields: {name:'Fox'},";
        s = s + "\nevents:{";
        s = s + "\n    fetch: 'org.nutz.ioc.json.pojo.WhenFetchAnimal',";
        s = s + "\n    create: 'org.nutz.ioc.json.pojo.WhenCreateAnimal',";
        s = s + "\n    depose: 'org.nutz.ioc.json.pojo.WhenDeposeAnimal'";
        s = s + "\n}";
        Ioc ioc = I(J("fox", s));

        Animal f = ioc.get(Animal.class, "fox");
        assertEquals(10, f.getCreateTime());
        assertEquals(10, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.get(Animal.class, "fox");

        assertEquals(10, f.getCreateTime());
        assertEquals(20, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.reset();
        assertEquals(10, f.getCreateTime());
        assertEquals(20, f.getFetchTime());
        assertEquals(10, f.getDeposeTime());
    }

    @Test
    public void test_events_by_trigger_for_un_singleton() {
        String s = "singleton:false, fields: {name:'Fox'},";
        s = s + "\nevents:{";
        s = s + "\n    fetch: 'org.nutz.ioc.json.pojo.WhenFetchAnimal',";
        s = s + "\n    create: 'org.nutz.ioc.json.pojo.WhenCreateAnimal',";
        s = s + "\n    depose: 'org.nutz.ioc.json.pojo.WhenDeposeAnimal'";
        s = s + "\n}";
        Ioc ioc = I(J("fox", s));

        Animal f = ioc.get(Animal.class, "fox");
        assertEquals(10, f.getCreateTime());
        assertEquals(10, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.get(Animal.class, "fox");

        assertEquals(10, f.getCreateTime());
        assertEquals(10, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.reset();
        assertEquals(10, f.getCreateTime());
        assertEquals(10, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());
    }

    @Test
    public void test_event_from_parent() {
        Ioc ioc = new NutIoc(new JsonLoader("org/nutz/ioc/json/events.js"));
        Animal f = ioc.get(Animal.class, "fox");
        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(0, f.getDeposeTime());

        ioc.depose();
        assertEquals(1, f.getCreateTime());
        assertEquals(1, f.getFetchTime());
        assertEquals(1, f.getDeposeTime());
    }
}
