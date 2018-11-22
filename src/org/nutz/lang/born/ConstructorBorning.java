package org.nutz.lang.born;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.nutz.lang.reflect.FastMethod;

public class ConstructorBorning<T> extends AbstractConstructorBorning implements Borning<T> {

    protected FastMethod fm;

    public ConstructorBorning(Constructor<T> c) {
        super(c);
    }

    @SuppressWarnings("unchecked")
    public T born(Object... args) {
    	try {
    	    return (T)call(args);
		} catch (InvocationTargetException e1) {
			throw new BorningException(e1.getTargetException(), c.getDeclaringClass(), args);
		} catch (Exception e) {
			if (e instanceof BorningException)
				throw (BorningException)e;
			throw new BorningException(e, c.getDeclaringClass(), args);
		}
    }

}
