package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.MatchType;
import org.nutz.lang.Mirror;

public abstract class AbstractFastClass implements FastClass {

	private static final String[] cMethodName = new String[]{"create", "newInstance"};
	
	public static final Class<?>[] EMTRY_PARAM_TYPES = new Class<?>[0];
	
	protected Class<?> clazz;
	protected Constructor<?>[] cs;
	protected Method[] methods;
	protected Field[] fields;
	protected Class<?>[][] csTypes;
	protected Class<?>[][] methodTypes;
	protected String[] methodNames;
	
	
	public AbstractFastClass(Class<?> clazz, Constructor<?>[] cs, Method[] methods, Field[] fields) {
	    this.clazz = clazz;
	    this.cs = cs;
	    this.methods = methods;
	    this.fields = fields;
	    this.csTypes = new Class<?>[cs.length][];
	    for (int i = 0; i < cs.length; i++) {
            csTypes[i] = cs[i].getParameterTypes();
        }
	    this.methodTypes = new Class<?>[methods.length][];
	    this.methodNames = new String[methods.length];
	    for (int i = 0; i < methods.length; i++) {
            methodTypes[i] = methods[i].getParameterTypes();
            methodNames[i] = methods[i].getName();
        }
	}
	//----------------------------------------------------
	// 子类需要覆盖的方法

	protected Object _born(int index, Object... args) {
		throw Lang.noImplement();
	}

	protected Object _invoke(Object obj, int index, Object... args) {
		throw Lang.noImplement();
	}

    public Object setField(Object obj, String fieldName, Object value) {
        throw Lang.noImplement();
    }

    public Object getField(Object obj, String fieldName) {
        throw Lang.noImplement();
    }
    
    //-----------------------------------------------------------

	public Object born(Constructor<?> constructor, Object... args) {
		if (constructor == null)
			throw new IllegalArgumentException("!!Constructor must not NULL !");
		if (Modifier.isPrivate(constructor.getModifiers()))
			throw new IllegalArgumentException("!!Constructor is private !");
		return _born(getConstructorIndex(constructor.getParameterTypes()), args);
	}

	public Object born(Class<?>[] types, Object... args) {
		int index = getConstructorIndex(types);
		if (index > -1)
			return _born(index, args);
		for (int i = 0; i < cMethodName.length; i++) {
			try {
				Method method = getSrcClass().getDeclaredMethod(cMethodName[i], types);
				if (Modifier.isPrivate(method.getModifiers()))
					continue;
				if (!Modifier.isStatic(method.getModifiers()))
					continue;
				if (!getSrcClass().isAssignableFrom(method.getReturnType()))
					continue;
				return invoke(null, method, args);
			}
			catch (Throwable e) {}
		}
		throw new IllegalArgumentException("!!Fail to find Constructor for args");
	}
	
	public Object born() {
	    return born(EMTRY_PARAM_TYPES);
	}

	private int getConstructorIndex(Class<?>[] cpB) {
		for (int i = 0; i < csTypes.length; i++) {
			Class<?>[] cpA = csTypes[i];
			if (MatchType.YES == Mirror.matchParamTypes(cpA, cpB))
				return i;
		}
		throw new RuntimeException("!!No such Constructor found!");
	}

	protected Constructor<?>[] getConstructors() {
	    return cs;
	}

	private int getMethodIndex(Method method) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].equals(method))
				return i;
			if (methods[i].getName().equals(method.getName()))
				if (MatchType.YES == Mirror.matchParamTypes(methodTypes[i],method.getParameterTypes()))
					return i;
		}
		throw new RuntimeException("!!No such Method found!");
	}

	private int getMethodIndex(String name, Class<?>[] cpB) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(name))
				if (MatchType.YES == Mirror.matchParamTypes(methodTypes[i],cpB))
					return i;
		}
		throw new RuntimeException("!!No such Method found!");
	}

	protected Class<?> getSrcClass() {
	    return clazz;
	}

	public Object invoke(Object obj, Method method, Object... args) {
		if (method == null)
			throw new IllegalArgumentException("!!Method must not NULL !");
		if (Modifier.isPrivate(method.getModifiers()))
			throw new IllegalArgumentException("!!Method is private !");
		if (obj == null && (Modifier.isStatic(method.getModifiers())) == false)
			throw new IllegalArgumentException("!!obj is NULL but Method isn't static !");
		int index = getMethodIndex(method);
		if (index > -1)
			return _invoke(obj, index, args);
		throw new IllegalArgumentException("!!No such method --> " + method);
	}

	public Object invoke(Object obj, String methodName, Class<?>[] types, Object... args) {
		int index = getMethodIndex(methodName, types);
		if (index > -1)
			return _invoke(obj, index, args);
		throw new IllegalArgumentException("!!Fail to get method ! For " + Json.toJson(types));
	}
}
