package org.nutz.lang.born;

import java.lang.reflect.Method;

import org.nutz.lang.Mirror;

public class DynaMethodBorning<T> implements Borning<T> {

    private Method method;

    public DynaMethodBorning(Method method) {
        this.method = method;
        this.method.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public T born(Object[] args) {
        try {
            return (T) method.invoke(null, Mirror.evalArgToRealArray(args));
        }
        catch (Exception e) {
            throw new BorningException(e, method.getDeclaringClass(), args);
        }
    }

}
