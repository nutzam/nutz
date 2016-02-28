package org.nutz.ioc.val;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocException;
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
		String[] names;
		if (ioc instanceof Ioc2)
			names = ((Ioc2)ioc).getNamesByType(type, ing.getContext());
		else
			names = ioc.getNamesByType(type);
		if (names == null || names.length == 0) {
			throw new IocException("can't found such type by name=[%s] or type=[%s]", name, type);
		}
		if (names.length > 1) {
			throw new IocException("more than one bean for type=[%s], names=%s", type, names);
		}
		if (ioc instanceof Ioc2)
			return ((Ioc2)ioc).getByType(type, ing.getContext());
		else
			return ioc.getByType(type);
	}

}
