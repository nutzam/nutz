package org.nutz.ioc.weaver;

import org.nutz.ioc.IocMaking;
import org.nutz.ioc.ValueProxy;
import org.nutz.lang.Mirror;
import org.nutz.lang.inject.Injecting;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class FieldInjector {
	
	private static final Log log = Logs.get();

    public static FieldInjector create(Mirror<?> mirror, String fieldName, ValueProxy vp, boolean optional) {
        FieldInjector fi = new FieldInjector();
        fi.valueProxy = vp;
        fi.inj = mirror.getInjecting(fieldName);
        fi.optional = optional;
        fi.fieldName = fieldName;
        return fi;
    }

    private ValueProxy valueProxy;
    private Injecting inj;
    private boolean optional;
    private String fieldName;

    private FieldInjector() {}

    void inject(IocMaking ing, Object obj) {
    	try {
    		Object value = valueProxy.get(ing);
            inj.inject(obj, value);
    	} catch (Throwable e) {
			if (optional) {
				log.info("field inject fail, but this field is optional, ignore error", e);
				return;
			}
			String msg = String.format("IocBean[%s] fail at field=[%s]", ing.getObjectName(), fieldName);
			throw new RuntimeException(msg, e);
		}
    }
}
