package com.zzh.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.LinkedList;

public class Mirror<T> {

	public static <T> Mirror<T> me(Class<T> classOfT) {
		if (null == classOfT)
			return null;
		return new Mirror<T>(classOfT);
	}

	private Class<T> klass;

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
		if (field.getType().equals("boolean"))
			try {
				return klass.getMethod("is" + fn);
			} catch (NoSuchMethodException e) {
			}
		return klass.getMethod("get" + fn);
	}

	public Method getSetter(Field field) throws NoSuchMethodException {
		return klass.getMethod("set" + Strings.capitalize(field.getName()), field.getType());
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
				throw new RuntimeException(e.getMessage());
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

	public T born() {
		try {
			return klass.newInstance();
		} catch (Exception e) {
			Class<?> oc = this.getMyOuterClass();
			if (null == oc)
				throw Lang.wrapThrow(e);
			// For inner class
			try {
				Constructor<T> c = klass.getDeclaredConstructor(oc);
				Object p = Mirror.me(oc).born();
				return c.newInstance(p);
			} catch (Exception e1) {
				throw Lang.wrapThrow(e1);
			}
		}
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
		if (klass == type)
			return true;
		return is(type.getName());
	}

	public boolean is(String className) {
		return klass.getName().equals(className);
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
			return Integer.class.getName().equals(klass.getName());
		}
		return klass.getSimpleName().toLowerCase().equals(type.getName());
	}

	public boolean canCastToDirectly(Class<?> type) {
		if (klass.isPrimitive() && type.isPrimitive()) {
			if (this.isPrimitiveNumber() && Mirror.me(type).isPrimitiveNumber())
				return true;
		}
		return (type.isAssignableFrom(klass)) || isWrpperOf(type)
				|| Mirror.me(type).isWrpperOf(klass);
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
