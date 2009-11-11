package org.nutz.ioc.val;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.meta.Pair;

public class ReferValue implements ValueProxy {

	private String name;
	private Class<?> type;

	public ReferValue(String name) {
		Pair<Class<?>> p = Iocs.parseName(name);
		this.name = p.getName();
		this.type = p.getValue();
	}

	public Object get(IocMaking ing) {
		return ing.getIoc().get(type, name);
	}

}
