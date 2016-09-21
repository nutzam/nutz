package org.nutz.resource.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;

public class ErrorResourceLocation extends ResourceLocation {
    
    public void scan(String base, Pattern pattern, List<NutResource> list) {}
    
    private static final Log log = Logs.get();
    
    private Object loc;
    
    public static ErrorResourceLocation make(Object loc) {
        if (loc == null) {
            log.debug("null scan path object");
        }
        return new ErrorResourceLocation(loc);
    }
    
    private ErrorResourceLocation(Object loc) {
        this.loc = loc;
        if (log.isInfoEnabled())
            log.info("[loc=" + loc + "]not exist");
    }
    public String toString() {
        return "ErrorResourceLocation [loc=" + loc + "]";
    }
    
    public String id() {
        return String.valueOf(loc);
    }
}