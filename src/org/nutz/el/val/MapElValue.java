package org.nutz.el.val;

import java.util.Map;

import org.nutz.el.El;
import org.nutz.el.ElValue;

public class MapElValue extends PojoElValue<Map<?, ?>> {

	public MapElValue(Map<?, ?> obj) {
		super(obj);
	}

	public ElValue getProperty(ElValue val) {
		return El.wrap(obj.get(val.getString()));
	}

}
