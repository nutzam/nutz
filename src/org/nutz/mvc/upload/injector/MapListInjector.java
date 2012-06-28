package org.nutz.mvc.upload.injector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;

public class MapListInjector implements ParamInjector {
    
    public MapListInjector(String name) {
        this.name = name;
    }

    private String name;

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Object obj = ((Map<?,?>) refer).get(name);
        if (obj == null)
            return null;
        
        if(obj instanceof List)
            return obj;
        
        List<Object> re = new ArrayList<Object>(1);
        re.add(obj);
        return re;
    }

}
