package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.nutz.json.JsonField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.eject.EjectByGetter;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.InjectBySetter;
import org.nutz.lang.inject.Injecting;

public class JsonEntityField {

    private String name;

    private Type genericType;

    private Injecting injecting;

    private Ejecting ejecting;

    /**
     * 根据名称获取字段实体, 默认以set优先
     */
    public static JsonEntityField eval(String name, Method getter, Method setter) {
        JsonEntityField jef = new JsonEntityField();
        jef.genericType = getter.getGenericReturnType();
        jef.name = name;
        jef.ejecting = new EjectByGetter(getter);
        jef.injecting = new InjectBySetter(setter);
        return jef;
    }

    public static JsonEntityField eval(Mirror<?> mirror, Field fld) {
        if (fld == null) {
            return null;
        }

        // 瞬时变量就不要持久化了
        if (Modifier.isTransient(fld.getModifiers()))
            return null;
    	
        // 以特殊字符开头的字段，看起来是隐藏字段
        // XXX 有用户就是_开头的字段也要啊! by wendal
    	//if (fld.getName().startsWith("_") || fld.getName().startsWith("$"))
    	if (fld.getName().startsWith("$") && fld.getAnnotation(JsonField.class) == null)
    		return null;
    	
        JsonField jf = fld.getAnnotation(JsonField.class);
        if (null != jf && jf.ignore())
            return null;

        JsonEntityField jef = new JsonEntityField();
        jef.genericType = Lang.getFieldType(mirror, fld);
        jef.name = Strings.sBlank(null == jf ? null : jf.value(), fld.getName());
        jef.ejecting = mirror.getEjecting(fld.getName());
        jef.injecting = mirror.getInjecting(fld.getName());

        return jef;
    }

    private JsonEntityField() {}

    public String getName() {
        return name;
    }

    public Type getGenericType() {
        return genericType;
    }

    public void setValue(Object obj, Object value) {
        if (injecting != null)
            injecting.inject(obj, value);
    }

    public Object getValue(Object obj) {
        if (ejecting == null)
            return null;
        return ejecting.eject(obj);
    }

}
