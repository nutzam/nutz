package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.Files;

public class FileValue implements ValueProxy {

    private String path;

    public FileValue(String path) {
        this.path = path;
    }

    public Object get(IocMaking ing) {
        return Files.findFile(path);
    }

}
