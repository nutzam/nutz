package org.nutz.json.entity;

import java.io.IOException;
import java.io.Writer;

import org.nutz.json.JsonFormat;

public interface JsonCallback {
    
    Object fromJson(Object obj);
    
    boolean toJson(Object obj, JsonFormat jf, Writer writer) throws IOException;
}
