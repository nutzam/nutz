package org.nutz.json.entity;

import java.io.IOException;
import java.io.Writer;

import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

public interface JsonCallback {
    
    boolean toJson(Object obj, JsonFormat jf, Writer writer, NutMap ctx) throws IOException;
}
