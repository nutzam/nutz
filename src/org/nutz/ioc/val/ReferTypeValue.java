package org.nutz.ioc.val;

import java.lang.reflect.Field;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.ioc.IocContext;
import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ReferTypeValue implements ValueProxy {
    
    private static Log log = Logs.get();
	
	protected String name;
	
	protected Class<?> type;
	
	protected boolean typeFirst;

	public ReferTypeValue() {
	}

    public ReferTypeValue(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
    
    public ReferTypeValue(Field field) {
        this.name = field.getName();
        this.type = field.getType();
        Inject inject = field.getAnnotation(Inject.class);
        if (inject != null)
            typeFirst = inject.typeFirst();
    }

	public Object get(IocMaking ing) {
		Ioc ioc = ing.getIoc();
		IocContext ctx = ing.getContext();
		if (typeFirst) {
		    String[] names;
		    if (ioc instanceof Ioc2) {
	            names = ((Ioc2)ioc).getNamesByType(type, ctx);
	            if (names != null && names.length == 1) {
	                return ((Ioc2)ioc).get(type, names[0], ctx);
	            }
		    }
	        else {
	            names = ioc.getNamesByType(type);
	            if (names != null && names.length == 1) {
                    return ioc.get(type, names[0]);
                }
	        }
		}
		if (ioc.has(name)) {
			if (ioc instanceof Ioc2)
				return ((Ioc2)ioc).get(type, name, ctx);
			return ioc.get(type, name);
		}
		if (log.isDebugEnabled())
		    log.debugf("name=%s not found, search for type=%s", name, type.getName());
		if (ioc instanceof Ioc2)
            return ((Ioc2)ioc).getByType(type, ctx);
        else
            return ioc.getByType(type);
	}
	
	public Object getByType(Ioc ioc, IocContext ctx) {
	    if (ioc instanceof Ioc2)
            return ((Ioc2)ioc).getByType(type, ctx);
        else
            return ioc.getByType(type);
	}
}
