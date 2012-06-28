package org.nutz.mvc.testapp.classes;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class TestSetup implements Setup {

    public void init(NutConfig config) {
        System.out.println(config.getAtMap().size());
    }

    public void destroy(NutConfig config) {

    }

}
