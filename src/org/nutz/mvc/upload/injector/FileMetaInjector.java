package org.nutz.mvc.upload.injector;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public class FileMetaInjector implements ParamInjector {

    public FileMetaInjector(String name) {
        this.name = name;
    }

    private String name;

    @SuppressWarnings("unchecked")
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return ((TempFile) ((Map<String, Object>) refer).get(name)).getMeta();
    }

}
