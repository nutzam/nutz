package org.nutz.dao.impl.entity;

import java.util.HashSet;
import java.util.Set;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.lang.util.AbstractContext;
import org.nutz.lang.util.Context;

public class EntityObjectContext extends AbstractContext {

    private static final String ME = "$me";

    private Entity<?> en;
    private Object obj;

    public EntityObjectContext(Entity<?> en, Object obj) {
        this.en = en;
        this.obj = obj;
    }

    public Context set(String name, Object value) {
        en.getField(name).setValue(obj, value);
        return this;
    }

    public Set<String> keys() {
        Set<String> names = new HashSet<String>(en.getMappingFields().size());
        names.add(ME);
        for (MappingField mf : en.getMappingFields())
            names.add(mf.getName());
        return names;
    }

    public boolean has(String key) {
        if (ME.equals(key))
            return true;
        return en.getField(key) != null;
    }

    public Context clear() {
        obj = en.getMirror().born();
        return this;
    }

    public Object get(String name) {
        if (ME.equals(name))
            return obj;
        return en.getField(name).getValue(obj);
    }

    public EntityObjectContext clone() {
        return new EntityObjectContext(en, obj);
    }
}
