package org.nutz.mvc.upload.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.upload.TempFile;

@Deprecated
public class FileMetaInjector extends AbstractUploadInjector {

    public FileMetaInjector(String name) {
        super(name);
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        if (refer == null)
            return null;
        TempFile tmp = getTempFile(refer, name);
        if (tmp == null)
        	return null;
        return tmp.getMeta();
    }

}
