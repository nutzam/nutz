package org.nutz.lang.inject;

import java.lang.reflect.Method;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;

public class InjectBySetter implements Injecting {

	private Method setter;
	private Class<?> valueType;

	public InjectBySetter(Method setter) {
		this.setter = setter;
		valueType = setter.getParameterTypes()[0];
	}

	public void inject(Object obj, Object value) {
		Object v = null;
		try {
			v = Castors.me().castTo(value, valueType);
			setter.invoke(obj, v);
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to set '%s' by setter %s.'%s()' because: %s", v, setter
					.getDeclaringClass().getName(), setter.getName(), e.getMessage());
		}
	}

}