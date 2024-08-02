package org.nutz.mvc.upload.injector;

import org.nutz.mvc.upload.TempFile;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TempFileInjector extends AbstractUploadInjector {

    public TempFileInjector(String name) {
        super(name);
    }

    @Override
    public TempFile get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        return getTempFile(refer, name);
    }

}
