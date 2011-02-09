package org.nutz.el.val;

import java.lang.reflect.Array;

import org.nutz.el.El;
import org.nutz.el.ElValue;

public class ArrayElValue extends PojoElValue<Object> {

	public ArrayElValue(Object obj) {
		super(obj);
	}

	public ElValue getProperty(ElValue val) {
		Object v = val.get();
		if (v instanceof Integer) {
			return El.wrap(Array.get(obj, ((Integer) v).intValue()));
		}
		return super.getProperty(val);
	}

}
