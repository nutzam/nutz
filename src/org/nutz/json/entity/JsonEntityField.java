package org.nutz.json.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.nutz.json.JsonField;
import org.nutz.lang.Mirror;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

public class JsonEntityField {

	private String name;

	private Type genericType;

	private Injecting injecting;

	private Ejecting ejecting;

	public JsonEntityField(Mirror<?> mirror, Field fld) {
		this.injecting = mirror.getInjecting(fld.getName());
		this.ejecting = mirror.getEjecting(fld.getName());
		this.genericType = fld.getGenericType();

		JsonField jf = fld.getAnnotation(JsonField.class);
		if (null != jf)
			name = jf.value();
		else
			name = fld.getName();

	}

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
