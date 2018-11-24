package org.nutz.lang.born;

import java.lang.reflect.Constructor;

public class EmptyArgsConstructorBorning<T> extends AbstractConstructorBorning implements Borning<T> {

    public EmptyArgsConstructorBorning(Constructor<T> c) {
        super(c);
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
        try {
            return (T) call();
        }
        catch (Exception e) {
            throw new BorningException(e, c.getDeclaringClass(), null);
        }
    }

}
