package org.nutz.castor;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 抽象转换器，所有的转换器必须继承自它
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <FROM>
 *            从什么类型
 * @param <TO>
 *            转到什么类型
 */
public abstract class Castor<FROM, TO> {

    protected Castor() {
        Class<?> myType = getClass();
        Class<?> mySuper = myType.getSuperclass();

        Type[] myParams = Mirror.getTypeParams(myType);
        Type[] superParams = Mirror.getTypeParams(mySuper);

        if (null != superParams && superParams.length == 2) {
            Class<?>[] args = new Class<?>[2];
            int n = 0;
            for (int i = 0; i < superParams.length; i++) {
                if (superParams[i] instanceof Class<?>)
                    args[i] = (Class<?>) superParams[i];
                else
                    args[i] = (Class<?>) myParams[n++];
            }
            fromClass = args[0];
            toClass = args[1];
        } else {
            fromClass = (Class<?>) myParams[0];
            toClass = (Class<?>) myParams[1];
        }

    }

    protected Class<?> fromClass;
    protected Class<?> toClass;

    public Class<?> getFromClass() {

        return fromClass;
    }

    public Class<?> getToClass() {
        return toClass;
    }

    public abstract TO cast(FROM src, Class<?> toType, String... args)
            throws FailToCastObjectException;

    @SuppressWarnings({"unchecked"})
    protected static Collection<?> createCollection(Object src, Class<?> toType)
            throws FailToCastObjectException {
        Collection<?> coll = null;
        try {
            coll = (Collection<Object>) toType.newInstance();
        }
        catch (Exception e) {
            if (Modifier.isAbstract(toType.getModifiers())) {
                if (toType.isAssignableFrom(ArrayList.class)) {
                    coll = new ArrayList<Object>(Array.getLength(src));
                } else if (toType.isAssignableFrom(HashSet.class)) {
                    coll = new HashSet<Object>();
                }
            }
            if (null == coll)
                throw new FailToCastObjectException(String.format("Castors don't know how to implement '%s'",
                                                                  toType.getName()),
                                                    Lang.unwrapThrow(e));
        }
        return coll;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Castor)) {
            return false;
        }
        Castor<?, ?> castor = (Castor<?, ?>) obj;
        return toString().equals(castor.toString());
    }

    public String toString() {
        return fromClass.getName() + "2" + toClass.getName();
    }

    public static final String key(Class<?> fromClass, Class<?> toClass) {
        return fromClass.getName() + "2" + toClass.getName();
    }
}
