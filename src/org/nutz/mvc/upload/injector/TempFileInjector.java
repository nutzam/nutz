package org.nutz.mvc.upload.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.upload.TempFile;

public class TempFileInjector extends AbstractUploadInjector {

    public TempFileInjector(String name) {
        super(name);
    }

    public TempFile get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return getTempFile(refer, name);
    }

}
