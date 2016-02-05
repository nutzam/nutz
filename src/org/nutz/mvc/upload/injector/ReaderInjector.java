package org.nutz.mvc.upload.injector;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.upload.TempFile;

public class ReaderInjector extends AbstractUploadInjector {

    public ReaderInjector(String name) {
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
			return Streams.buffr(new InputStreamReader(tmp.getInputStream()));
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
    }

}
