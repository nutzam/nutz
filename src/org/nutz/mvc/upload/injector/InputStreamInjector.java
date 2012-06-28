package org.nutz.mvc.upload.injector;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Streams;

public class InputStreamInjector extends FileInjector {

    public InputStreamInjector(String name) {
        super(name);
    }

    @Override
    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        File f = getFile(refer);
        return Streams.buff(Streams.fileIn(f));
    }

}
