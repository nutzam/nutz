package org.nutz.dao.sql;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface VarSet extends Serializable {

    VarSet set(String name, Object value);

    VarSet putAll(Map<String, Object> map);
    
    VarSet putAll(Object pojo);

    Object get(String name);

    Set<String> keys();

    int size();
}
