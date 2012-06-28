package org.nutz.dao.impl.sql.pojo;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.SqlType;

public abstract class AbstractPItem implements PItem {

    protected Pojo pojo;

    public Pojo getPojo() {
        return pojo;
    }

    public void setPojo(Pojo pojo) {
        this.pojo = pojo;
        this.setupPojo(pojo);
    }

    protected SqlType getSqlType() {
        return pojo.getSqlType();
    }

    protected FieldMatcher getFieldMatcher() {
        return pojo.getContext().getFieldMatcher();
    }

    protected void setupPojo(Pojo pojo) {}

    protected Entity<?> _en(Entity<?> en) {
        if (null == en && null != pojo)
            return pojo.getEntity();
        return en;
    }

    protected String _fmtcolnm(Entity<?> en, String name) {
        if (null == en && null != pojo)
            en = pojo.getEntity();

        if (null != en) {
            MappingField mf = en.getField(name);
            if (null != mf)
                return mf.getColumnName();
        }
        return name;
    }

}
