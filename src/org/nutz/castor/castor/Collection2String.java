package org.nutz.castor.castor;

import java.util.Collection;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

@SuppressWarnings({"rawtypes"})
public class Collection2String extends Castor<Collection, String> {

    @Override
    public String cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return Json.toJson(src, JsonFormat.compact());
    }

}
