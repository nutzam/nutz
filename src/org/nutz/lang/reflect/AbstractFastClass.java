package org.nutz.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.MatchType;
import org.nutz.lang.Mirror;

public abstract class AbstractFastClass implements FastClass {

	private static final String[] cMethodName = new String[]{"create", "newInstance"};

	private static final Class<?>[] toClasses(Object... args) {
		Class<?>[] classes = new Class[args.length];
		for (int i = 0; i < classes.length; i++)
			classes[i] = args[i].getClass();
		return classes;
	}

	protected Object _born(int index, Object... args) {
		throw Lang.noImplement();
	}

	protected Object _invoke(Object obj, int index, Object... args) {
		throw Lang.noImplement();
	}

	public Object born(Constructor<?> constructor, Object... args) {
		if (constructor == null)
			throw new IllegalArgumentException("!!Constructor must not NULL !");
		if (Modifier.isPrivate(constructor.getModifiers()))
			throw new IllegalArgumentException("!!Constructor is private !");
		return _born(getConstructorIndex(constructor.getParameterTypes()), args);
	}

	public Object born(Object... args) {
		Class<?>[] classes = toClasses(args);
		int index = getConstructorIndex(classes);
		if (index > -1)
			return _born(getConstructorIndex(classes), args);
		for (int i = 0; i < cMethodName.length; i++) {
			try {
				Method method = getSrcClass().getDeclaredMethod(cMethodName[i], classes);
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

	private int getConstructorIndex(Class<?>[] cpB) {
		Constructor<?>[] constructors = getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			Class<?>[] cpA = constructors[i].getParameterTypes();
			if (MatchType.YES == Mirror.matchParamTypes(cpA, cpB))
				return i;
		}
		throw new RuntimeException("!!No such Constructor found!");
	}

	protected abstract Constructor<?>[] getConstructors();

	private int getMethodIndex(Method method) {
		for (int i = 0; i < getMethods().length; i++) {
			if (getMethods()[i].equals(method))
				return i;
			if (getMethods()[i].getName().equals(method.getName()))
				if (MatchType.YES == Mirror.matchParamTypes(getMethods()[i].getParameterTypes(),
															method.getParameterTypes()))
					return i;
		}
		throw new RuntimeException("!!No such Method found!");
	}

	private int getMethodIndex(String name, Class<?>[] cpB) {
		for (int i = 0; i < getMethods().length; i++) {
			if (getMethods()[i].getName().equals(name))
				if (MatchType.YES == Mirror.matchParamTypes(getMethods()[i].getParameterTypes(),
															cpB))
					return i;
		}
		throw new RuntimeException("!!No such Method found!");
	}

	protected abstract Method[] getMethods();

	protected abstract Class<?> getSrcClass();

	public Object invoke(Object obj, Method method, Object... args) {
		if (method == null)
			throw new IllegalArgumentException("!!Method must not NULL !");
		if (Modifier.isPrivate(method.getModifiers()))
			throw new IllegalArgumentException("!!Method is private !");
		if (obj == null && (Modifier.isStatic(method.getModifiers())) == false)
			throw new IllegalArgumentException("!!obj is NULL but Method isn't static !");
		int index = getMethodIndex(method);
		if (index > -1)
			return _invoke(obj, getMethodIndex(method), args);
		throw new IllegalArgumentException("!!No such method --> " + method);
	}

	public Object invoke(Object obj, String methodName, Object... args) {
		Class<?>[] classes = toClasses(args);
		int index = getMethodIndex(methodName, classes);
		if (index > -1)
			return _invoke(obj, index, args);
		throw new IllegalArgumentException("!!Fail to get method ! For " + Json.toJson(classes));
	}
}
