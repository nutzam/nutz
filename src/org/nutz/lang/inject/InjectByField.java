package org.nutz.lang.inject;

import java.lang.reflect.Field;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

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
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to set '%s' to field %s.'%s' because: %s", v, field
					.getDeclaringClass().getName(), field.getName(), e.getMessage());
		}
	}
}
