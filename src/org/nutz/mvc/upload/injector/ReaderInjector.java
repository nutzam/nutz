package org.nutz.mvc.upload.injector;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Streams;

/**
 * @since 1.r.55开始使用与servlet 3.0+一致的Part接口,原方法标记为弃用.
 */
@Deprecated
public class ReaderInjector extends FileInjector {

    public ReaderInjector(String name) {
        super(name);
    }

    @Override
    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        if (refer == null)
            return null;
        File f = getFile(refer);
        return Streams.buffr(Streams.fileInr(f));
    }

}
