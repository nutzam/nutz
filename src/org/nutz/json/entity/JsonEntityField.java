package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.nutz.json.JsonField;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

public class JsonEntityField {

	private String name;

	private Type genericType;

	private Injecting injecting;

	private Ejecting ejecting;

	public static JsonEntityField eval(Mirror<?> mirror, Field fld) {
		JsonField jf = fld.getAnnotation(JsonField.class);
		if (null != jf && jf.ignore())
			return null;

		JsonEntityField jef = new JsonEntityField();
		jef.injecting = mirror.getInjecting(fld.getName());
		jef.ejecting = mirror.getEjecting(fld.getName());
		jef.genericType = fld.getGenericType();

		if (null != jf && !Strings.isBlank(jf.value()))
			jef.name = jf.value();
		else
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

}
