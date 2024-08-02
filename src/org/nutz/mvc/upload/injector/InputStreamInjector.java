package org.nutz.mvc.upload.injector;

import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.mvc.upload.TempFile;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InputStreamInjector extends AbstractUploadInjector {

    public InputStreamInjector(String name) {
        super(name);
    }

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        if (refer == null) {
            return null;
        }
        TempFile tmp = getTempFile(refer, name);
        if (tmp == null) {
            return null;
        }
        try {
            return tmp.getInputStream();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

}
