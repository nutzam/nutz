package org.nutz.lang.born;

import java.lang.reflect.Method;

public class MethodBorning<T> implements Borning<T> {

    private Method method;

    public MethodBorning(Method method) {
        this.method = method;
        this.method.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public T born(Object[] args) {
        try {
            return (T) method.invoke(null, args);
        }
        catch (Exception e) {
            throw new BorningException(e, method.getDeclaringClass(), args);
        }
    }

}
