package org.nutz.aop.v2.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Type {
	
	public static String descriptor(Object obj) {
		StringBuilder sb = new StringBuilder();
		if (obj instanceof Class<?>)
			getDescriptor(sb, (Class<?>)obj);
		else if (obj instanceof Method)
			getDescriptor(sb, (Method)obj);
		else if (obj instanceof Constructor<?>)
			getDescriptor(sb, (Constructor<?>)obj);
		else 
			throw new RuntimeException();
		return sb.toString();
	}

	public static void getDescriptor(StringBuilder sb ,Method method){
		sb.append('(');
		for (Class<?> klass : method.getParameterTypes())
			getDescriptor(sb, klass);
		sb.append(')');
		getDescriptor(sb, method.getReturnType());
	}
	
	public static void getDescriptor(StringBuilder sb , Constructor<?> constructor){
		sb.append('(');
		for (Class<?> klass : constructor.getParameterTypes())
			getDescriptor(sb, klass);
		sb.append(')');
		sb.append('V');
	}
	
	/**本方法来源于ow2的asm库的Type类*/
	public static void getDescriptor(final StringBuilder buf, final Class<?> c) {
        Class<?> d = c;
        while (true) {
            if (d.isPrimitive()) {
                char car;
                if (d == Integer.TYPE) {
                    car = 'I';
                } else if (d == Void.TYPE) {
                    car = 'V';
                } else if (d == Boolean.TYPE) {
                    car = 'Z';
                } else if (d == Byte.TYPE) {
                    car = 'B';
                } else if (d == Character.TYPE) {
                    car = 'C';
                } else if (d == Short.TYPE) {
                    car = 'S';
                } else if (d == Double.TYPE) {
                    car = 'D';
                } else if (d == Float.TYPE) {
                    car = 'F';
                } else /* if (d == Long.TYPE) */{
                    car = 'J';
                }
                buf.append(car);
                return;
            } else if (d.isArray()) {
                buf.append('[');
                d = d.getComponentType();
            } else {
                buf.append('L');
                String name = d.getName();
                int len = name.length();
                for (int i = 0; i < len; ++i) {
                    char car = name.charAt(i);
                    buf.append(car == '.' ? '/' : car);
                }
                buf.append(';');
                return;
            }
        }
    }
}
