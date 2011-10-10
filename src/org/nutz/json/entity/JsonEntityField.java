package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.nutz.json.JsonField;
import org.nutz.json.JsonParsing;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
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

	@SuppressWarnings("deprecation")
	public static JsonEntityField eval(Mirror<?> mirror, Field fld) {
		JsonField jf = fld.getAnnotation(JsonField.class);
		if (null != jf && jf.ignore())
			return null;

		JsonEntityField jef = new JsonEntityField();
		jef.genericType = fld.getGenericType();
		
		//看看有没有指定获取方式
		if (jf != null) {
			String getBy = jf.getBy();
			if (Strings.isBlank(getBy))
				getBy = jf.by();
			if (!Strings.isBlank(jf.by()))
				jef.ejecting = new EjectBySimpleEL(getBy);
			if (!Strings.isBlank(jf.value()))
				jef.name = jf.value();
			if (!Strings.isBlank(jf.createBy()))
				jef.createBy = jf.createBy();
			jef.hasAnno = true;
		}
		if (null == jef.ejecting )
			jef.ejecting = mirror.getEjecting(fld.getName());
		if (null == jef.injecting)
			jef.injecting = mirror.getInjecting(fld.getName());
		if (null == jef.name)
			jef.name = fld.getName();

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
		injecting.inject(obj, value);
	}

	public Object getValue(Object obj) {
		return ejecting.eject(obj);
	}

	public Object createValue(Object holder, Object value) {
		if (this.createBy == null)
			return JsonParsing.convert(genericType, value);
//		try {
//			return genericType.getMethod(createBy, holder.getClass(), Type.class, Object.class).invoke(null, holder,genericType,value);
//		} catch (Throwable e){}
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
