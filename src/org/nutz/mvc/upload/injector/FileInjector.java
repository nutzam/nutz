package org.nutz.mvc.upload.injector;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.upload.TempFile;

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
        if (tmp == null)
        	return null;
        return tmp.getFile();
    }

    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        return getFile(refer);
    }

}
