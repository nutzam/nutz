package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.nutz.json.JsonField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Objs;
import org.nutz.lang.Strings;
import org.nutz.lang.eject.EjectBySimpleEL;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

public class JsonEntityField {

	private String name;

	private Type genericType;

	private Injecting injecting;

	private Ejecting ejecting;
	
	private String createBy;
	
	private boolean hasAnno;

	/**
	 * 根据名称获取字段实体, 默认以set优先
	 * @param mirror
	 * @param name
	 * @return
	 */
	public static JsonEntityField eval(Mirror<?> mirror, String name){
	    Method[] methods = mirror.findSetters(name);
	    if(methods.length == 1){
	        Type[] types = Lang.getMethodParamTypes(mirror, methods[0]);
	        JsonEntityField jef = new JsonEntityField();
	        jef.genericType = types[0];
	        fillJef(jef, mirror, name);
	    }else {
	        try {
                return eval(mirror, mirror.getField(name));
            } catch (NoSuchFieldException e) {
                return null;
            }
	    }
	    return null;
	}
	@SuppressWarnings("deprecation")
	public static JsonEntityField eval(Mirror<?> mirror, Field fld) {
	    if(fld == null){
	        return null;
	    }
		JsonField jf = fld.getAnnotation(JsonField.class);
		if (null != jf && jf.ignore())
			return null;
		//瞬时变量就不要持久化了
		if (Modifier.isTransient(fld.getModifiers()))
			return null;

		JsonEntityField jef = new JsonEntityField();
	    jef.genericType = Lang.getFieldType(mirror, fld);
		
		//看看有没有指定获取方式
		if (jf != null) {
			String getBy = jf.getBy();
			if (Strings.isBlank(getBy))
				getBy = jf.by();
			if (!Strings.isBlank(getBy))
				jef.ejecting = new EjectBySimpleEL(getBy);
			if (!Strings.isBlank(jf.value()))
				jef.name = jf.value();
			if (!Strings.isBlank(jf.createBy()))
				jef.createBy = jf.createBy();
			jef.hasAnno = true;
		}
		fillJef(jef, mirror, fld.getName());

		return jef;
	}
	
	private static void fillJef(JsonEntityField jef, Mirror<?> mirror, String name){
	    if (null == jef.ejecting )
            jef.ejecting = mirror.getEjecting(name);
        if (null == jef.injecting)
            jef.injecting = mirror.getInjecting(name);
        if (null == jef.name)
            jef.name = name;
	}

	private JsonEntityField() {}

	public String getName() {
		return name;
	}

	public Type getGenericType() {
		return genericType;
	}

	public void setValue(Object obj, Object value) {
		injecting.inject(obj, value);
	}

	public Object getValue(Object obj) {
		return ejecting.eject(obj);
	}

	public Object createValue(Object holder, Object value) {
		if (this.createBy == null)
		    return Objs.convert(value, genericType);
		try {
			return holder.getClass().getMethod(createBy, Type.class, Object.class).invoke(holder, genericType, value);
		} catch (Throwable e){
			throw Lang.wrapThrow(e);
		}
	}
	
	public boolean hasAnno() {
		return hasAnno;
	}
}
