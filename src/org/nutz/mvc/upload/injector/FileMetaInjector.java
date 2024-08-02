package org.nutz.mvc.upload.injector;

import org.nutz.mvc.upload.TempFile;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Deprecated
public class FileMetaInjector extends AbstractUploadInjector {

    public FileMetaInjector(String name) {
        super(name);
    }

    @Override
    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        if (refer == null) {
            return null;
        }
        TempFile tmp = getTempFile(refer, name);
        if (tmp == null) {
            return null;
        }
        return tmp.getMeta();
    }

}
