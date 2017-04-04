package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface FastClass {

	String CLASSNAME = "$$FASTCLASS";

	Object invoke(Object obj, Method method, Object... args);

	Object invoke(Object obj, String methodName, Class<?>[] types, Object... args);

	Object born(Constructor<?> constructor, Object... args);

	Object born(Class<?>[] types, Object... args);
	
	Object born();

	Object setField(Object obj, String fieldName, Object value);

	Object getField(Object obj, String fieldName);
	
	FastMethod fast(Method method);
	
	FastMethod fast(Constructor<?> constructor);

}