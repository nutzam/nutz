package org.nutz.resource.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;

public class ErrorResourceLocation extends ResourceLocation {
    public void scan(String base, Pattern pattern, List<NutResource> list) {}
    
    private static final Log log = Logs.get();
    
    Object loc;
    
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((loc == null) ? 0 : loc.hashCode());
        return result;
    }
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ErrorResourceLocation other = (ErrorResourceLocation) obj;
        if (loc == null) {
            if (other.loc != null)
                return false;
        } else if (!loc.equals(other.loc))
            return false;
        return true;
    }
}