package org.nutz.lang.inject;

import java.lang.reflect.Field;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class InjectByField implements Injecting {
    
    private static final Log log = Logs.get();

    private Field field;

    public InjectByField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            v = Castors.me().castTo(value, field.getType());
            field.set(obj, v);
        }
        catch (Exception e) {
            if (log.isInfoEnabled())
                log.info("Fail to set value by field", e);
            throw Lang.makeThrow(    "Fail to set '%s'[ %s ] to field %s.'%s' because [%s]: %s",
                                    value,
                                     v,
                                    field.getDeclaringClass().getName(),
                                    field.getName(),
                                    Lang.unwrapThrow(e),
                                    Lang.unwrapThrow(e).getMessage());
        }
    }
}
