package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.nutz.lang.Streams;

public abstract class NutResource {

    public boolean equals(Object obj) {
    	if (obj == null)
    		return false;
        if (this == obj)
            return true;
        if (obj instanceof NutResource)
            return this.toString().equals(obj.toString());
        return false;
    }

    /**使用完毕后,务必关闭*/
    public abstract InputStream getInputStream() throws IOException;

    public String getName() {
        return name;
    }

    /**使用完毕后,务必关闭*/
    public Reader getReader() throws IOException {
        return Streams.utf8r(getInputStream());
    }

    public int hashCode() {
        return null == name ? "NULL".hashCode() : name.hashCode();
    }

    public NutResource setName(String name) {
        this.name = name;
        return this;
    }

    public String toString() {
        return String.format("NutResource[%s]", name);
    }

    protected String name;

}
