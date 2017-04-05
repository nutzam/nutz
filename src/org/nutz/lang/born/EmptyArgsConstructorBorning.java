package org.nutz.lang.born;

import java.lang.reflect.Constructor;

import org.nutz.conf.NutConf;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;

public class EmptyArgsConstructorBorning<T> implements Borning<T> {

    private Constructor<T> c;
    
    private FastMethod fm;

    public EmptyArgsConstructorBorning(Constructor<T> c) {
        this.c = c;
        if (!c.isAccessible())
            this.c.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
        try {
            if (NutConf.USE_FASTCLASS) {
                if (fm == null)
                    fm = FastClassFactory.get(c);
                return (T) fm.invoke(null);
            }
            return c.newInstance();
        }
        catch (Exception e) {
            throw new BorningException(e, c.getDeclaringClass(), null);
        }
    }

}
