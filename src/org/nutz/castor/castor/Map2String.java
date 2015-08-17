package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

@SuppressWarnings({"rawtypes"})
public class Map2String extends Castor<Map, String> {

    @Override
    public String cast(Map src, Class<?> toType, String... args) throws FailToCastObjectException {
        return Json.toJson(src, JsonFormat.tidy());
    }

}
