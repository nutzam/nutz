package com.zzh.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Mirror<T> {

	public static <T> Mirror<T> me(Class<T> classOfT) {
		return null == classOfT ? null : new Mirror<T>(classOfT);
	}

	public static <T> Mirror<T> me(Class<T> classOfT, TypeExtractor typeExtractor) {
		return new Mirror<T>(classOfT).setTypeExtractor(typeExtractor);
	}

	private Class<T> klass;

	private TypeExtractor typeExtractor;

	public Mirror<T> setTypeExtractor(TypeExtractor typeExtractor) {
		this.typeExtractor = typeExtractor;
		return this;
	}

	private Mirror(Class<T> classOfT) {
		if (null == classOfT)
			throw new RuntimeException("Mirror can not accept NULL!!!");
		klass = classOfT;
	}

	public Method getGetter(String fieldName) throws NoSuchMethodException {
		String fn = Strings.capitalize(fieldName);
		Method m;
		try {
			m = klass.getMethod("get" + fn);
		} catch (NoSuchMethodException e) {
			m = klass.getMethod("is" + fn);
			try {
				if (!getField(fieldName).getType().getName().equals("boolean")) {
					throw new NoSuchMethodException(String.format(
							"Field '%s.%s' should be a boolean type", klass.getName(), fieldName));
				}
			} catch (NoSuchFieldException e1) {
				throw new NoSuchMethodException(String.format("Field '%s.%s' doesn't exist!", klass
						.getName(), fieldName));
			}

		}
		return m;
	}

	public Method getGetter(Field field) throws NoSuchMethodException {
		String fn = Strings.capitalize(field.getName());
		if (Mirror.me(field.getType()).is(boolean.class))
			try {
				return klass.getMethod("is" + fn);
			} catch (NoSuchMethodException e) {
				try {
					return klass.getMethod(field.getName());
				} catch (NoSuchMethodException e1) {
				}
			}
		return klass.getMethod("get" + fn);
	}

	public Method getSetter(Field field) throws NoSuchMethodException {
		try {
			return klass.getMethod("set" + Strings.capitalize(field.getName()), field.getType());
		} catch (NoSuchMethodException e) {
			if (field.getName().startsWith("is") && Mirror.me(field.getType()).is(boolean.class))
				return klass.getMethod("set" + field.getName().substring(2), field.getType());
			throw e;
		}
	}

	public Field getField(String name) throws NoSuchFieldException {
		Class<?> theClass = klass;
		Field f;
		while (null != theClass && !(theClass == Object.class)) {
			try {
				f = theClass.getDeclaredField(name);
				return f;
			} catch (NoSuchFieldException e) {
				theClass = theClass.getSuperclass();
			}
		}
		throw new NoSuchFieldException(String.format(
				"Can NOT find field [%s] in class [%s] and it's parents classes", name, klass
						.getName()));
	}

	public <AT extends Annotation> Field getField(Class<AT> ann) throws NoSuchFieldException {
		for (Field field : this.getFields()) {
			if (null != field.getAnnotation(ann))
				return field;
		}
		throw new NoSuchFieldException(String.format(
				"Can NOT find field [@%s] in class [%s] and it's parents classes", ann.getName(),
				klass.getName()));
	}

	private static boolean isIgnoredField(Field f) {
		if (Modifier.isStatic(f.getModifiers()))
			return true;
		if (Modifier.isFinal(f.getModifiers()))
			return true;
		if (f.getName().startsWith("this$"))
			return true;
		return false;
	}

	public Field[] getFields() {
		Class<?> theClass = klass;
		LinkedList<Field> list = new LinkedList<Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i]))
					continue;
				list.add(fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.toArray(new Field[list.size()]);
	}

	public Method[] getMethods() {
		Class<?> theClass = klass;
		LinkedList<Method> list = new LinkedList<Method>();
		while (null != theClass && !(theClass == Object.class)) {
			Method[] fs = theClass.getMethods();
			for (int i = 0; i < fs.length; i++) {
				list.add(fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.toArray(new Method[list.size()]);
	}

	public void setValue(Object obj, Field field, Object value) {
		try {
			field.set(obj, value);
		} catch (Exception e) {
			try {
				Method setter = getSetter(field);
				setter.invoke(obj, value);
			} catch (Exception e1) {
				throw Lang.wrapThrow(e);
			}
		}
	}

	public Object getValue(Object obj, Field f) {
		try {
			return f.get(obj);
		} catch (Exception e) {
			try {
				return this.getGetter(f).invoke(obj);
			} catch (Exception e1) {
				throw new RuntimeException(e1.getMessage());
			}
		}

	}

	public Object getValue(Object obj, String name) {
		try {
			return this.getGetter(name).invoke(obj);
		} catch (Exception e) {
			try {
				return this.getValue(obj, this.getField(name));
			} catch (NoSuchFieldException e1) {
				throw Lang.wrapThrow(e1);
			}
		}
	}

	public Class<T> getMyClass() {
		return klass;
	}

	public Class<?> extractType() {
		Class<?> re = null != typeExtractor ? typeExtractor.extract(this) : null;
		if (null != re)
			return re;
		if (this.isNumber())
			return Number.class;
		else if (this.isBoolean())
			return Boolean.class;
		else if (this.isChar())
			return Character.class;
		else if (this.klass.isArray())
			return Array.class;
		else if (Map.class.isAssignableFrom(klass))
			return Map.class;
		else if (Collection.class.isAssignableFrom(klass))
			return Collection.class;
		else if (Calendar.class.isAssignableFrom(klass))
			return Calendar.class;
		else if (Timestamp.class.isAssignableFrom(klass))
			return Timestamp.class;
		else if (java.sql.Date.class.isAssignableFrom(klass))
			return java.sql.Date.class;
		else if (java.sql.Time.class.isAssignableFrom(klass))
			return java.sql.Time.class;
		else if (java.util.Date.class.isAssignableFrom(klass))
			return java.util.Date.class;
		return Object.class;
	}

	@SuppressWarnings("unchecked")
	public Class<T> getWrpperClass() {
		if (!klass.isPrimitive()) {
			if (this.isPrimitiveNumber() || this.is(Boolean.class) || this.is(Character.class))
				return klass;
			throw new RuntimeException(String.format("Class '%s' should be a primitive class",
					klass.getName()));
		}
		if (is(int.class))
			return (Class<T>) Integer.class;
		if (is(char.class))
			return (Class<T>) Character.class;
		try {
			return (Class<T>) Class.forName("java.lang." + Strings.capitalize(klass.getName()));
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Class<?> getMyOuterClass() {
		if (Modifier.isStatic(klass.getModifiers()))
			return null;
		String name = klass.getName();
		int pos = name.lastIndexOf('$');
		if (pos == -1)
			return null;
		name = name.substring(0, pos);
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public T born(Object... args) {
		Class<?> oc = this.getMyOuterClass();
		Constructor<?> c = null;
		ArrayList<Object> argList = null;
		if (null == oc) {
			argList = Lang.fill(new ArrayList<Object>(args.length + 1), args);
		} else {
			if (args.length > 0 && Mirror.me(args[0].getClass()).is(oc))
				argList = Lang.fill(new ArrayList<Object>(), args);
			else {
				argList = Lang.fill(new ArrayList<Object>(), new Object[] { Mirror.me(oc).born() },
						args);
			}
		}
		c = findConstructor(argList);
		try {
			return (T) c.newInstance(argList.toArray(new Object[argList.size()]));
		} catch (Exception e) {
			throw new FailToBornException(e, klass, args);
		}
	}

	public Constructor<?> findConstructor(List<Object> args) {
		Constructor<?> c = null;
		Class<?>[] paramTypes = new Class<?>[args.size()];
		for (int i = 0; i < args.size(); i++)
			paramTypes[i] = args.get(i).getClass();
		try {
			c = klass.getConstructor(paramTypes);
		} catch (NoSuchMethodException e) {
			// look for a constructor
			for (Constructor<?> cc : klass.getConstructors()) {
				Class<?>[] cTypes = cc.getParameterTypes();
				boolean yesItis;
				if (cTypes.length == paramTypes.length) {
					yesItis = doMatchConstrucctor(paramTypes, cTypes);
				} else if (cTypes.length == paramTypes.length + 1
						&& cTypes[paramTypes.length].isArray()) {
					yesItis = doMatchConstrucctor(paramTypes, cTypes);
					args.add(Array.newInstance(cTypes[paramTypes.length].getComponentType(), 0));
				} else
					continue;
				if (yesItis) {
					c = cc;
					break;
				}

			}
			if (c == null)
				throw new FailToBornException(e, klass, args.toArray(new Object[args.size()]));
		}
		return c;
	}

	private static boolean doMatchConstrucctor(Class<?>[] paramTypes, Class<?>[] cTypes) {
		for (int i = 0; i < paramTypes.length; i++)
			if (Mirror.me(paramTypes[i]).canCastToDirectly((cTypes[i])))
				return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public T duplicate(T src) {
		Method m;
		try {
			m = klass.getMethod("clone");
			return (T) m.invoke(src);
		} catch (Exception failToClone) {
			try {
				T obj = born();
				Field[] fields = getFields();
				for (Field field : fields) {
					Object value = getValue(src, field);
					setValue(obj, field, value);
				}
				return obj;

			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

	public boolean is(Class<?> type) {
		if (null == type)
			return false;
		if (klass == type)
			return true;
		return is(type.getName());
	}

	public boolean is(String className) {
		return klass.getName().equals(className);
	}

	public boolean isOf(Class<?> type) {
		return type.isAssignableFrom(klass);
	}

	public boolean isString() {
		return is(String.class);
	}

	public boolean isStringLike() {
		return CharSequence.class.isAssignableFrom(klass);
	}

	public boolean isChar() {
		return is(char.class) || is(Character.class);
	}

	public boolean isBoolean() {
		return is(boolean.class) || is(Boolean.class);
	}

	public boolean isFloat() {
		return is(float.class) || is(Float.class);
	}

	public boolean isDouble() {
		return is(double.class) || is(Double.class);
	}

	public boolean isInt() {
		return is(int.class) || is(Integer.class);
	}

	public boolean isInteger() {
		return isInt() || isLong() || isShort() || isByte();
	}

	public boolean isDecimal() {
		return isFloat() || isDouble();
	}

	public boolean isLong() {
		return is(long.class) || is(Long.class);
	}

	public boolean isShort() {
		return is(short.class) || is(Short.class);
	}

	public boolean isByte() {
		return is(byte.class) || is(Byte.class);
	}

	public boolean isWrpperOf(Class<?> type) {
		if (!type.isPrimitive())
			return false;
		if ("int".equals(type.getName())) {
			return is(Integer.class);
		}
		return klass.getSimpleName().toLowerCase().equals(type.getName());
	}

	public boolean canCastToDirectly(Class<?> type) {
		if (type.isAssignableFrom(klass))
			return true;
		if (klass.isPrimitive() && type.isPrimitive()) {
			if (this.isPrimitiveNumber() && Mirror.me(type).isPrimitiveNumber())
				return true;
		}
		return isWrpperOf(type) || Mirror.me(type).isWrpperOf(klass);
	}

	public boolean isPrimitiveNumber() {
		return isInt() || isLong() || isFloat() || isDouble() || isByte() || isShort();
	}

	public boolean isNumber() {
		return Number.class.isAssignableFrom(klass) || klass.isPrimitive() && !is(boolean.class);
	}

	public boolean isDateTimeLike() {
		return Calendar.class.isAssignableFrom(klass)
				|| java.util.Date.class.isAssignableFrom(klass)
				|| java.sql.Timestamp.class.isAssignableFrom(klass)
				|| java.sql.Date.class.isAssignableFrom(klass)
				|| java.sql.Time.class.isAssignableFrom(klass);
	}

	public static Type[] getTypeParams(Class<?> klass) {
		Type superclass = klass.getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		return ((ParameterizedType) superclass).getActualTypeArguments();
	}

}
