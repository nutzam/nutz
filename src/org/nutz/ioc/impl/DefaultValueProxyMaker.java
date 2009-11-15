package org.nutz.ioc.impl;

import java.util.Collection;
import java.util.Map;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.ValueProxyMaker;
import org.nutz.ioc.meta.IocObject;
import org.nutz.ioc.meta.IocValue;
import org.nutz.ioc.val.*;

public class DefaultValueProxyMaker implements ValueProxyMaker {

	@SuppressWarnings("unchecked")
	public ValueProxy make(IocMaking ing, IocValue iv) {
		Object value = iv.getValue();
		String type = iv.getType();
		// Null
		if ("null".equals(type) || null == value) {
			return new StaticValue(null);
		}
		// String, Number, .....
		else if ("normal".equals(type) || null == type) {
			// Array
			if (value.getClass().isArray()) {
				return new ArrayValue(ing, (IocValue[]) value);
			}
			// Map
			else if (value instanceof Map<?, ?>) {
				return new MapValue(ing, (Map<String, IocValue>) value,
						(Class<? extends Map<String, Object>>) value.getClass());
			}
			// Collection
			else if (value instanceof Collection<?>) {
				return new CollectionValue(ing, (Collection<IocValue>) value,
						(Class<? extends Collection<Object>>) value.getClass());
			}
			// Inner Object
			else if (value instanceof IocObject) {
				return new InnerValue((IocObject) value);
			}
			return new StaticValue(value);
		}
		// Refer
		else if ("refer".equals(type)) {
			String s = value.toString();
			// $Ioc
			if ("$ioc".equalsIgnoreCase(s)) {
				return new IocSelfValue();
			}
			// $Name
			else if ("$name".equalsIgnoreCase(s)) {
				return new ObjectNameValue();
			}
			return new ReferValue(s);
		}
		// Java
		else if ("java".equals(type)) {
			return new JavaValue(value.toString());
		}
		// File
		else if ("file".equals(type)) {
			return new FileValue(value.toString());
		}
		// Env
		else if ("env".equals(type)) {
			return new EnvValue(value.toString());
		}
		// Inner
		else if ("inner".equals(type)) {
			return new InnerValue((IocObject) value);
		}
		return null;
	}

}
