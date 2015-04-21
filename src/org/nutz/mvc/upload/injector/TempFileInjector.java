package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public class TempFileInjector implements ParamInjector {

    public TempFileInjector(String name) {
        this.name = name;
    }

    private String name;

    @SuppressWarnings("unchecked")
    public TempFile get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Object tmp = ((Map<String, Object>) refer).get(name);
        if (tmp != null && tmp instanceof TempFile)
            return (TempFile)tmp;
        return null;
    }

}
