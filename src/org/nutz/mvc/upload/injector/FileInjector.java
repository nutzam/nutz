package org.nutz.mvc.upload.injector;

import java.io.File;

import org.nutz.mvc.upload.TempFile;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @since 1.r.55开始使用与servlet 3.0+一致的Part接口,原方法标记为弃用.
 */
@Deprecated
public class FileInjector extends AbstractUploadInjector {

    public FileInjector(String name) {
        super(name);
    }

    protected File getFile(Object refer) {
        TempFile tmp = getTempFile(refer, name);
        if (tmp == null) {
            return null;
        }
        return tmp.getFile();
    }

    @Override
    public Object get(ServletContext sc,
                      HttpServletRequest req,
                      HttpServletResponse resp,
                      Object refer) {
        return getFile(refer);
    }

}
