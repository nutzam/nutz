package org.nutz.dao.interceptor.impl;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.entity.macro.ElFieldMacro;
import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class SimpleElPojoInterceptor extends BasicPojoInterceptor {

    protected static Log log = Logs.get();

    protected String elStr;

    protected ElFieldMacro macro;

    protected String event;

    protected String selfStr;

    protected MappingField mf;

    protected El el;

    /**
     * 当前字段如果不为默认值时才起效
     * 如果设置 nullEffective = true ：则 当前字段==null 时起效
     */
    protected boolean nullEffective;

    protected SimpleElPojoInterceptor() {
    }

    public SimpleElPojoInterceptor(MappingField mf, String str, String event) {
        this.el = new El(str);
        this.mf = mf;
        this.event = event;
        this.elStr = str;
        this.selfStr = String.format("%s - %s - %s - %s - %s", mf.getEntity().getType().getSimpleName(), mf.getName(), event, this.elStr, this.nullEffective);
    }

    public SimpleElPojoInterceptor(MappingField mf, String str, String event, boolean nullEffective) {
        this.el = new El(str);
        this.mf = mf;
        this.event = event;
        this.elStr = str;
        this.nullEffective = nullEffective;
        this.selfStr = String.format("%s - %s - %s - %s - %s", mf.getEntity().getType().getSimpleName(), mf.getName(), event, this.elStr, this.nullEffective);
    }

    public void onEvent(Object obj, Entity<?> en, String event, Object... args) {
        if (event.equals(this.event)) {
            if (this.nullEffective)
                if (null != mf.getValue(obj))
                    return;
            Context context = Lang.context();
            context.set("field", mf.getColumnName());
            context.set("view", mf.getEntity());
            context.set("$me", obj);
            Object elVal = el.eval(context);
            if (elVal != null)
                mf.setValue(obj, elVal);
        }
    }

    @Override
    public String toString() {
        return selfStr;
    }
}
