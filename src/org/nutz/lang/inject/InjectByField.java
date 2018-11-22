package org.nutz.lang.inject;

import org.nutz.castor.Castors;
import org.nutz.lang.reflect.ReflectTool;

import java.lang.reflect.Field;

public class InjectByField implements Injecting {

    private Field field;

    public InjectByField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public void inject(Object obj, Object value) {
        Object v = null;
        try {
            //获取泛型基类中的字段真实类型, https://github.com/nutzam/nutz/issues/1288
            Class<?> ft = ReflectTool.getGenericFieldType(obj.getClass(), field);
            v = Castors.me().castTo(value, ft);
            field.set(obj, v);
        }
        catch (Exception e) {
            String msg = String.format("Fail to set field[%s#%s] using value[%s]", field.getDeclaringClass().getName(), field.getName(), value);
            throw new RuntimeException(msg, e);
        }
    }
}
