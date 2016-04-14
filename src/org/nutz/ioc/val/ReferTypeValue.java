package org.nutz.ioc.val;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;

public class ReferTypeValue implements ValueProxy {
	
	protected String name;
	
	protected Class<?> type;

	public ReferTypeValue() {
	}
	
	public ReferTypeValue(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

	public Object get(IocMaking ing) {
		Ioc ioc = ing.getIoc();
		if (ioc.has(name)) {
			if (ioc instanceof Ioc2)
				return ((Ioc2)ioc).get(type, name, ing.getContext());
			return ioc.get(type, name);
		}
		if (ioc instanceof Ioc2)
			return ((Ioc2)ioc).getByType(type, ing.getContext());
		else
			return ioc.getByType(type);
	}

}
