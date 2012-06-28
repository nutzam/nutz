package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.nutz.lang.Streams;

public abstract class NutResource implements Comparable<NutResource> {

    public int compareTo(NutResource o) {
        if (o == null)
            return -1;
        if (this == o || (this.name == null && o.name == null))
            return 0;
        if (this.name != null && o.name != null)
            return name.compareTo(o.getName());
        return this.name == null ? 1 : -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof NutResource)
            return 0 == compareTo((NutResource) obj);
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

    @Override
    public int hashCode() {
        return null == name ? "NULL".hashCode() : name.hashCode();
    }

    public NutResource setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return String.format("NutResource[%s]", name);
    }

    protected String name;

}
