package org.nutz.mvc.upload.injector;

import java.util.List;
import java.util.Map;

import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.upload.TempFile;

public abstract class AbstractUploadInjector implements ParamInjector {
	
	protected String name;
	
	public AbstractUploadInjector(String name) {
		this.name = name;
	}

    @SuppressWarnings("unchecked")
	protected TempFile getTempFile(Object refer, String name) {
    	if (refer == null)
            return null;
        Object obj = ((Map<String, Object>) refer).get(name);
        if (obj == null)
            return null;

        // Map 中只有可能有两种值， TempFile 或者 List<TempFile>
        // 如果是单一对象直接返回
        if (obj instanceof TempFile) {
            return (TempFile) obj;
        }
        else if (obj instanceof String)
            return null;
        // 如果是列表，则取第一项
        else {
            List<?> list = (List<?>) obj;
            if (list.isEmpty())
                return null;
            else
                return (TempFile) list.get(0);
        }
    }
}
