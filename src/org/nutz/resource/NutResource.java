package org.nutz.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.nutz.lang.Streams;

public abstract class NutResource implements Comparable<NutResource> {

    protected String name;

    /**
     * 这个对象的来源
     */
    protected String source;
    
    protected int priority = 100;
    
    public NutResource() {}

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
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSource() {
        return source;
    }
    
    public int compareTo(NutResource o) {
        if (o.priority == this.priority)
            return 0;
        return o.priority > this.priority ? -1 : 1;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public NutResource setPriority(int priority) {
        this.priority = priority;
        return this;
    }
}
