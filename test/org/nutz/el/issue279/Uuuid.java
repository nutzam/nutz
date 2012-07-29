package org.nutz.el.issue279;

import java.util.List;

import org.nutz.el.opt.RunMethod;
import org.nutz.plugin.Plugin;

public class Uuuid implements RunMethod, Plugin {

    public boolean canWork() {
        return true;
    }

    public Object run(List<Object> fetchParam) {
        return "abc";
    }

    public String fetchSelf() {
        return "uuuid";
    }

}
