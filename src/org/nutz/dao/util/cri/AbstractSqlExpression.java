package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.sql.pojo.AbstractPItem;

public abstract class AbstractSqlExpression extends AbstractPItem implements SqlExpression {

    protected boolean not;

    private String name;

    protected AbstractSqlExpression(String name) {
        this.name = name;
    }

    AbstractSqlExpression not() {
        this.not = true;
        return this;
    }

    public SqlExpression setNot(boolean not) {
        this.not = not;
        return this;
    }

    protected String _fmtcol(Entity<?> en) {
        return _fmtcolnm(en, name);
    }

    protected MappingField _field(Entity<?> en) {
        en = _en(en);
        return null == en ? null : en.getField(name);
    }

}
