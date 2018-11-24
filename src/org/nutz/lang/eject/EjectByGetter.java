package org.nutz.lang.eject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.nutz.conf.NutConf;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Lang;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class EjectByGetter implements Ejecting {

    private static final Log log = Logs.get();

    private Method getter;
    
    protected FastMethod fm;

    public EjectByGetter(Method getter) {
        this.getter = getter;
    }

    public Object eject(Object obj) {
        try {
            if (obj == null)
                return null;
            if (NutConf.USE_FASTCLASS) {
                if (fm == null)
                    fm = FastClassFactory.get(getter);
                if (fm == null)
                	return getter.invoke(obj);
                return fm.invoke(obj);
            }
            return getter.invoke(obj);
        }
        catch (InvocationTargetException e) {
            throw new FailToGetValueException("getter=" + getter, e);
        }
        catch (Exception e) {
            if (log.isInfoEnabled())
                log.info("Fail to value by getter", e);
            throw Lang.makeThrow(    "Fail to invoke getter %s.'%s()' %s because [%s]: %s",
                                    getter.getDeclaringClass().getName(),
                                    getter.getName(),
                                    (obj == null || getClass().getDeclaringClass() == obj.getClass() ? "" : "<"+obj.getClass()+">"),
                                    Lang.unwrapThrow(e),
                                    Lang.unwrapThrow(e).getMessage());
        }
    }

}
