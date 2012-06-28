package org.nutz.ioc.json;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.pojo.Animal;
import org.nutz.ioc.loader.map.MapLoader;
import org.nutz.lang.Lang;

class Utils {

    static Ioc2 I(String... ss) {
        String json = "{";
        json += Lang.concat(',', ss);
        json += "}";
        return new NutIoc(new MapLoader(json));
    }

    static String J(String name, String s) {
        return name + " : {" + s + "}";
    }

    static Animal A(String s) {
        Ioc ioc = I(J("obj", s));
        return ioc.get(Animal.class, "obj");
    }

}
