package org.nutz.el.val;

import java.lang.reflect.Array;
import java.math.BigDecimal;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class ArrayElValue extends PojoElValue<Object> {

	public ArrayElValue(Object obj) {
		super(obj);
	}

	public ElValue getProperty(ElValue val) {
		Object v = val.get();
		if (v instanceof BigDecimal) {
			return El.wrap(Array.get(obj, ((BigDecimal) v).intValue()));
		} else if ("length".equals(val.getString())) {
			return El.wrap(Array.getLength(obj));
		}
		throw new ElException("Array obj can not support property '%s'", val.getString());
	}

}
