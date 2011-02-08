package org.nutz.el.val;

import java.util.List;

import org.nutz.el.El;
import org.nutz.el.ElValue;

public class ListElValue extends PojoElValue<List<?>> {

	public ListElValue(List<?> obj) {
		super(obj);
	}

	public ElValue getProperty(ElValue val) {
		return El.wrap(obj.get(val.getInteger()));
	}

}
