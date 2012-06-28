package org.nutz.mvc.adaptor.injector;

import org.nutz.mvc.adaptor.ParamInjector;

public abstract class AttrInjector implements ParamInjector {

    protected String name;

    protected AttrInjector(String name) {
        this.name = name;
    }

}
