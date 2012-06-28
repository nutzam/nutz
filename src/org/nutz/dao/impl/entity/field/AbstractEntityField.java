package org.nutz.dao.impl.entity.field;

import java.lang.reflect.Type;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

public abstract class AbstractEntityField implements EntityField {

    private Entity<?> entity;

    private String name;

    private Type type;

    private Class<?> typeClass;

    private Mirror<?> mirror;

    private Injecting injecting;

    private Ejecting ejecting;

    public AbstractEntityField(Entity<?> entity) {
        this.entity = entity;
    }

    public Entity<?> getEntity() {
        return entity;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public Mirror<?> getTypeMirror() {
        return mirror;
    }

    public void setValue(Object obj, Object value) {
        injecting.inject(obj, value);
    }

    public Object getValue(Object obj) {
        return ejecting.eject(obj);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInjecting(Injecting injecting) {
        this.injecting = injecting;
    }

    public void setEjecting(Ejecting ejecting) {
        this.ejecting = ejecting;
    }

    public void setType(Type type) {
        this.type = type;
        this.typeClass = Lang.getTypeClass(type);
        this.mirror = Mirror.me(typeClass);
    }

    public String toString() {
        return String.format("'%s'(%s)", this.name, this.entity.getType().getName());
    }

}
