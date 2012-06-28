package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class ConstructorBorning<T> implements Borning<T> {

    private Constructor<T> c;

    public ConstructorBorning(Constructor<T> c) {
        this.c = c;
        this.c.setAccessible(true);
    }

    public T born(Object[] args) {
        try {
            return c.newInstance(args);
        }
        catch (Exception e) {
            throw new BorningException(e, c.getDeclaringClass(), args);
        }
    }

}
