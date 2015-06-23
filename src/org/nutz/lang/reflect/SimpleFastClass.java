package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.lang.Lang;

/**
 * 用于调试的FastClass原型
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class SimpleFastClass extends AbstractFastClass {

    public SimpleFastClass(Class<?> clazz, Constructor<?>[] cs, Method[] methods, Field[] fields) {
        super(clazz, cs, methods, fields);
    }

    protected Object _born(int index, Object... args) {
        switch (index) {
        case 0:
            return toString();
        case 1:
            return hashCode();
        case 200:
            return false;
        case 10000:
            return "zzz";
        }
        throw Lang.impossible();
    }
}
