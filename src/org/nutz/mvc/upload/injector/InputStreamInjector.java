package org.nutz.mvc.upload.injector;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.mvc.upload.TempFile;

public class InputStreamInjector extends AbstractUploadInjector {

    public InputStreamInjector(String name) {
        super(name);
    }

    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        if (refer == null)
            return null;
        TempFile tmp = getTempFile(refer, name);
        if (tmp == null)
        	return null;
        try {
			return tmp.getInputStream();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
    }

}
