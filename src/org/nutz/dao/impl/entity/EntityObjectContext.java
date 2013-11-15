package org.nutz.dao.impl.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.lang.util.AbstractContext;
import org.nutz.lang.util.Context;

public class EntityObjectContext extends AbstractContext {

    private static final String ME = "$me";

    private Map<String, Object> ext = new HashMap<String, Object>();

    private Entity<?> en;
    private Object obj;

    public EntityObjectContext(Entity<?> en, Object obj) {
        this.en = en;
        this.obj = obj;
    }

    public int size() {
        return ext.size();
    }

    public Context set(String name, Object value) {
        MappingField field = en.getField(name);
        if (field != null)
            field.setValue(obj, value);
        else
            ext.put(name, value);
        return this;
    }

    public Set<String> keys() {
        Set<String> names = new HashSet<String>(en.getMappingFields().size());
        names.add(ME);
        for (MappingField mf : en.getMappingFields())
            names.add(mf.getName());
        names.addAll(ext.keySet());
        return names;
    }

    public boolean has(String key) {
        if (ME.equals(key))
            return true;
        if (en.getField(key) != null)
            return true;
        return ext.containsKey(key);
    }

    public Context clear() {
        obj = en.getMirror().born();
        ext.clear();
        return this;
    }

    public Object get(String name) {
        if (ME.equals(name))
            return obj;
        MappingField field = en.getField(name);
        if (field != null)
            return field.getValue(obj);
        return ext.get(name);
    }

    public EntityObjectContext clone() {
        EntityObjectContext eoc = new EntityObjectContext(en, obj);
        if (!this.ext.isEmpty())
            eoc.ext = new HashMap<String, Object>(this.ext);
        return eoc;
    }
}
