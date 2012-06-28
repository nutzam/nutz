package org.nutz.mvc.upload.injector;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public class FileInjector implements ParamInjector {

    public FileInjector(String name) {
        this.name = name;
    }

    private String name;

    @SuppressWarnings("unchecked")
    protected File getFile(Object refer) {
        Object obj = ((Map<String, Object>) refer).get(name);
        if (obj == null)
            return null;

        // Map 中只有可能有两种值， TempFile 或者 List<TempFile>
        // 如果是单一对象直接返回
        if (obj instanceof TempFile) {
            return ((TempFile) obj).getFile();
        }
        // 如果是列表，则取第一项
        else {
            List<?> list = (List<?>) obj;
            if (list.isEmpty())
                return null;
            else
                return ((TempFile) list.get(0)).getFile();
        }
    }

    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        return getFile(refer);
    }

}
