package com.zzh.lang;

public class Objects {
/*
	public static Method getCloneMethod(Class<?> klass) {
		try {
			return klass.getMethod("clone", (Class<?>[]) null);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isPrimitive(Class<?> type) {
		if (type.isPrimitive())
			return true;
		else if (type == Boolean.class)
			return true;
		else if (type == Character.class)
			return true;
		else if (type == Byte.class)
			return true;
		else if (type == Short.class)
			return true;
		else if (type == Integer.class)
			return true;
		else if (type == Long.class)
			return true;
		else if (type == Float.class)
			return true;
		else if (type == Double.class)
			return true;
		return false;
	}

	public static Method getGetter(Class<?> type, Field field) throws NoSuchMethodException {
		Method m = null;
		String fn = field.getName();
		if ("boolean".equals(field.getType().getName())) {
			m = type.getMethod("is"
					+ Strings.capitalize((fn.startsWith("is") ? fn.substring(2) : fn)),
					(Class[]) null);
		} else {
			m = type.getMethod("get" + Strings.capitalize(fn), (Class[]) null);
		}
		if (m.getReturnType() != field.getType())
			return null;
		return m;
	}

	public static Method getGetter(Class<?> type, String name) throws NoSuchMethodException {
		Method m = null;
		try {
			m = type.getMethod("get" + Strings.capitalize(name));
			if (isBoolean(m.getReturnType()))
				m = null;
		} catch (NoSuchMethodException e) {
		}
		if (null == m)
			try {
				m = type.getMethod("is" + Strings.capitalize(name));
				if (!isBoolean(m.getReturnType()))
					m = null;
			} catch (NoSuchMethodException e1) {
			}
		if (null == m)
			throw new NoSuchMethodException("can not find getter for '" + name + "' in class '"
					+ type.getName() + "'");
		return m;
	}

	public static Method getSetter(Class<?> type, Field field) throws SecurityException,
			NoSuchMethodException {
		return getSetter(type, field.getName(), field.getType());
	}

	public static Method getSetter(Class<?> type, String fn, Class<?> argType)
			throws NoSuchMethodException {
		if ("boolean".equals(argType.getName())) {
			return type.getMethod("set"
					+ Strings.capitalize((fn.startsWith("is") ? fn.substring(2) : fn)), argType);
		}
		return type.getMethod("set" + Strings.capitalize(fn), argType);
	}

	public static Field[] getAllMyStdFields(Class<?> klass) {
		Class<?> theClass = klass;
		LinkedList<Field> list = new LinkedList<Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i]))
					continue;
				if (fs[i].getName().charAt(0) == '_')
					continue;
				list.add(fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.toArray(new Field[list.size()]);
	}

	public static Field getField(Class<?> klass, String name) {
		Class<?> theClass = klass;
		while (null != theClass && !(theClass == Object.class)) {
			Field f;
			try {
				f = theClass.getDeclaredField(name);
				return f;
			} catch (Exception e) {
				theClass = theClass.getSuperclass();
			}
		}
		return null;
	}

	public static boolean hasField(Class<?> klass, String name) {
		return null != getField(klass, name);
	}

	public static Object getFieldValue(Object obj, Field f) {
		if (null == obj || null == f)
			return null;
		Object v = getFieldValueByGetter(obj, f);
		if (null != v)
			return v;
		try {
			return f.get(obj);
		} catch (Exception e) {
			return null;
		}

	}

	public static Object getFieldValue(Object obj, String fn) {
		// if there are the field
		Object re = getFieldValue(obj, getField(obj.getClass(), fn));
		// try getter
		if (null == re) {
			try {
				Method getter = Objects.getGetter(obj.getClass(), fn);
				re = getter.invoke(obj);
			} catch (Exception e) {
			}
		}
		return re;
	}

	public static Object getFieldValueByGetter(Object obj, Field f) {
		try {
			return Objects.getGetter(obj.getClass(), f).invoke(obj, (Object[]) null);
		} catch (Exception e) {
			return null;
		}
	}

	public static Field[] getAllMyFields(Class<?> klass) {
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

	private static boolean isIgnoredField(Field f) {
		if (Modifier.isStatic(f.getModifiers()))
			return true;
		if (Modifier.isFinal(f.getModifiers()))
			return true;
		if (f.getName().startsWith("this$"))
			return true;
		return false;
	}

	public static Method[] getAllMyMethods(Class<?> klass) {
		int size = 0;
		Class<?> theClass = klass;
		while (null != theClass) {
			size += theClass.getDeclaredMethods().length;
			theClass = theClass.getSuperclass();
		}
		Method[] methods = new Method[size];
		theClass = klass;
		int index = 0;
		while (null != theClass) {
			Method[] ms = theClass.getDeclaredMethods();
			for (int i = 0; i < ms.length; i++) {
				index += i;
				methods[index] = ms[i];
			}
			theClass = theClass.getSuperclass();
		}
		return methods;
	}

	public static boolean isInteger(Class<?> type) {
		if (type == Byte.class || "byte".equals(type.getName()))
			return true;
		else if (type == Short.class || "short".equals(type.getName()))
			return true;
		else if (type == Integer.class || "int".equals(type.getName()))
			return true;
		else if (type == Long.class || "long".equals(type.getName()))
			return true;
		return false;
	}

	public static boolean isFloat(Class<?> type) {
		if (type == Float.class || "float".equals(type.getName()))
			return true;
		else if (type == Double.class || "double".equals(type.getName()))
			return true;
		return false;
	}

	public static boolean isBoolean(Class<?> type) {
		if (type == Boolean.class || "boolean".equals(type.getName()))
			return true;
		return false;
	}

	public static Object duplicate(Object obj) {
		return duplicateAs(obj, obj.getClass());
	}

	public static Object duplicateAs(Object obj, Class<?> type) {
		Object o = newInstance(type);
		Field[] fs = getAllMyStdFields(obj.getClass());
		for (int i = 0; i < fs.length; i++) {
			Object v = getFieldValue(obj, fs[i]);
			if (null == v)
				continue;
			try {
				Method setter = getSetter(type, fs[i]);
				setter.invoke(o, v);
			} catch (Exception e) {
			}
		}
		return o;
	}

	// @ TODO ZZH: think about multiple level inner class.
	public static <T> T newInstance(Class<T> klass) {
		if (null == klass)
			return null;
		try {
			return klass.newInstance();
		} catch (Exception e) {
			Class<?> oc = getOuterClass(klass);
			if (null == oc)
				return null;
			// For inner class
			try {
				Constructor<T> c = klass.getDeclaredConstructor(oc);
				Object p = newInstance(oc);
				return c.newInstance(p);
			} catch (Exception e1) {
				return null;
			}
		}
	}

	public static Class<?> getOuterClass(Class<?> klass) {
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

	public static void writeToFile(Object obj, File f) throws IOException {
		Writer w = null;
		try {
			Files.createNewFile(f);
			w = new FileWriter(f);
			w.write(obj.toString());
			w.close();
		} catch (IOException e) {
			Streams.safeClose(w);
			throw e;
		}
	}
*/
}
