package org.nutz.dao.interceptor.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.DB;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.interceptor.PojoInterceptor;
import org.nutz.dao.interceptor.annotation.PrevDelete;
import org.nutz.dao.interceptor.annotation.PrevInsert;
import org.nutz.dao.interceptor.annotation.PrevUpdate;
import org.nutz.dao.jdbc.JdbcExpert;

public class DefaultPojoInterceptor extends BasicPojoInterceptor {

    protected List<PojoInterceptor> list = new LinkedList<PojoInterceptor>();

    protected JdbcExpert expert;

    protected Entity<?> en;

    public void setupEntity(Entity<?> en, JdbcExpert expert) {
        this.expert = expert;
        this.en = en;
        Field[] fields = en.getMirror().getFields();
        for (Field field : fields) {
            MappingField mf = en.getField(field.getName());
            if (mf != null)
                setupField(mf, field);
        }
    }

    protected void setupField(MappingField mf, Field field) {
        for (Annotation anno : field.getAnnotations()) {
            setupFieldAnnotation(mf, field, anno);
        }
    }

    protected void setupFieldAnnotation(MappingField mf, Field field, Annotation anno) {
        if (anno instanceof PrevInsert) {
            setupFieldEL(mf, field, ((PrevInsert)anno).els(), "prevInsert",((PrevInsert)anno).nullEffective());
            if (((PrevInsert)anno).now()) {
                list.add(new SimpleElPojoInterceptor(mf, "now()", "prevInsert",((PrevInsert)anno).nullEffective()));
            }
            if (((PrevInsert)anno).uu32()) {
                list.add(new SimpleElPojoInterceptor(mf, "uuid()", "prevInsert",((PrevInsert)anno).nullEffective()));
            }
        }
        else if (anno instanceof PrevUpdate) {
            setupFieldEL(mf, field, ((PrevUpdate)anno).els(), "prevUpdate",((PrevUpdate)anno).nullEffective());
            if (((PrevUpdate)anno).now()) {
                list.add(new SimpleElPojoInterceptor(mf, "now()", "prevUpdate",((PrevUpdate)anno).nullEffective()));
            }
        }
        else if (anno instanceof PrevDelete) {
            setupFieldEL(mf, field, ((PrevDelete)anno).els(), "prevDelete",false);
        }
    }

    protected void setupFieldEL(MappingField mf, Field field, EL[] els, String event,boolean nullEffective) {
        EL e = null;
        for (EL el : els) {
            if (el.db() == DB.OTHER && e == null)
                e = el;
            else if (el.db().name().equals(expert.getDatabaseType()))
                e = el;
        }
        if (e != null) {
            list.add(new SimpleElPojoInterceptor(mf, e.value(), event, nullEffective));
        }
    }

    @Override
    public void onEvent(Object obj, Entity<?> en, String event, Object... args) {
        for (PojoInterceptor pint : list) {
            pint.onEvent(obj, en, event, args);
        }
    }

    @Override
    public boolean isAvailable() {
        return !list.isEmpty();
    }

    public List<PojoInterceptor> getList() {
        return list;
    }

    public void setList(List<PojoInterceptor> list) {
        this.list = list;
    }
}
