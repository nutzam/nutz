package org.nutz.lang.born;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.nutz.lang.Mirror;

public class DynamicConstructorBorning<T> extends AbstractConstructorBorning implements Borning<T> {

    public DynamicConstructorBorning(Constructor<T> c) {
        super(c);
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
        try {
            return (T) call(Mirror.evalArgToRealArray(args));
        } catch (InvocationTargetException e1) {
			throw new BorningException(e1.getTargetException(), c.getDeclaringClass(), args);
		} catch (Exception e) {
			if (e instanceof BorningException)
				throw (BorningException)e;
			throw new BorningException(e, c.getDeclaringClass(), args);
		}
    }

}
