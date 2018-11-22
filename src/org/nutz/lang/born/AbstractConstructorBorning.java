package org.nutz.lang.born;

import java.lang.reflect.Constructor;

import org.nutz.conf.NutConf;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;

public abstract class AbstractConstructorBorning {

    protected Constructor<?> c;
    protected FastMethod fm;
    
    public AbstractConstructorBorning(Constructor<?> c) {
        super();
        if (!c.isAccessible())
            c.setAccessible(true);
        this.c = c;
    }
    
    protected Object call(Object...args) throws Exception {
        if (NutConf.USE_FASTCLASS) {
            if (fm == null)
                fm = FastClassFactory.get(c);
            return fm.invoke(null, args);
        }
        return c.newInstance(args);
    }
}
