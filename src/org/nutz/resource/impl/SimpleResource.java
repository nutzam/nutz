package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;

import org.nutz.resource.NutResource;

public class SimpleResource extends NutResource {
    
    protected InputStream ins;
    
    public SimpleResource() {}
    
    public SimpleResource(String name, String source, InputStream ins) {
        super();
        this.ins = ins;
        this.name = name;
        this.source = source;
    }


    public InputStream getInputStream() throws IOException {
        return ins;
    }

    public void setInputStream(InputStream ins) {
        this.ins = ins;
    }
}
