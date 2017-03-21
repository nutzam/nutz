package org.nutz.lang.inject;

import java.lang.reflect.Field;

import org.nutz.castor.Castors;

public class InjectByField implements Injecting {

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
            String msg = String.format("Fail to set field[%s#%s] using value[%s]", field.getDeclaringClass().getName(), field.getName(), value);
            throw new RuntimeException(msg, e);
        }
    }
}
