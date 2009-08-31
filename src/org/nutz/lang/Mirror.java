package org.nutz.lang;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;

public class Mirror<T> {

	private static class DefaultTypeExtractor implements TypeExtractor {

		public Class<?>[] extract(Mirror<?> mirror) {
			ArrayList<Class<?>> re = new ArrayList<Class<?>>(5);
			re.add(mirror.getType());
			if (mirror.klass.isEnum())
				re.add(Enum.class);

			else if (mirror.klass.isArray())
				re.add(Array.class);

			else if (mirror.isString())
				re.add(String.class);

			else if (mirror.is(Class.class))
				re.add(Class.class);

			else if (mirror.is(Mirror.class))
				re.add(Mirror.class);

			else if (mirror.isStringLike())
				re.add(CharSequence.class);

			else if (mirror.isNumber()) {
				re.add(mirror.getType());
				re.add(Number.class);

			} else if (mirror.isBoolean())
				re.add(Boolean.class);

			else if (mirror.isChar())
				re.add(Character.class);

			else if (mirror.isOf(Map.class))
				re.add(Map.class);

			else if (mirror.isOf(Collection.class))
				re.add(Collection.class);

			else if (mirror.isOf(Calendar.class))
				re.add(Calendar.class);

			else if (mirror.isOf(Timestamp.class))
				re.add(Timestamp.class);

			else if (mirror.isOf(java.sql.Date.class))
				re.add(java.sql.Date.class);

			else if (mirror.isOf(java.sql.Time.class))
				re.add(java.sql.Time.class);

			else if (mirror.isOf(java.util.Date.class))
				re.add(java.util.Date.class);

			re.add(Object.class);
			return re.toArray(new Class<?>[re.size()]);
		}

	}

	private final static DefaultTypeExtractor defaultTypeExtractor = new DefaultTypeExtractor();

	public static <T> Mirror<T> me(Class<T> classOfT) {
		return null == classOfT ? null : new Mirror<T>(classOfT)
				.setTypeExtractor(defaultTypeExtractor);
	}

	public static <T> Mirror<T> me(Class<T> classOfT, TypeExtractor typeExtractor) {
		return null == classOfT ? null : new Mirror<T>(classOfT)
				.setTypeExtractor(typeExtractor == null ? defaultTypeExtractor : typeExtractor);
	}

	private Class<T> klass;

	private TypeExtractor typeExtractor;

	public Mirror<T> setTypeExtractor(TypeExtractor typeExtractor) {
		this.typeExtractor = typeExtractor;
		return this;
	}

	private Mirror(Class<T> classOfT) {
		klass = classOfT;
	}

	public Method getGetter(String fieldName) throws NoSuchMethodException {
		try {
			String fn = Strings.capitalize(fieldName);
			try {
				try {
					return klass.getMethod("get" + fn);
				} catch (NoSuchMethodException e) {
					Method m = klass.getMethod("is" + fn);
					if (!Mirror.me(m.getReturnType()).isBoolean())
						throw new NoSuchMethodException();
					return m;
				}
			} catch (NoSuchMethodException e) {
				return klass.getMethod(fieldName);
			}
		} catch (Exception e) {
			throw Lang.makeThrow(NoSuchMethodException.class, "Fail to find getter for [%s]->[%s]",
					klass.getName(), fieldName);
		}
	}

	public Method getGetter(Field field) throws NoSuchMethodException {
		try {
			try {
				String fn = Strings.capitalize(field.getName());
				if (Mirror.me(field.getType()).is(boolean.class))
					return klass.getMethod("is" + fn);
				else
					return klass.getMethod("get" + fn);
			} catch (NoSuchMethodException e) {
				return klass.getMethod(field.getName());
			}
		} catch (Exception e) {
			throw Lang.makeThrow(NoSuchMethodException.class, "Fail to find getter for [%s]->[%s]",
					klass.getName(), field.getName());
		}
	}

	public Method getSetter(Field field) throws NoSuchMethodException {
		try {
			try {
				return klass
						.getMethod("set" + Strings.capitalize(field.getName()), field.getType());
			} catch (Exception e) {
				try {
					if (field.getName().startsWith("is")
							&& Mirror.me(field.getType()).is(boolean.class))
						return klass.getMethod("set" + field.getName().substring(2), field
								.getType());
					return klass.getMethod(field.getName(), field.getType());
				} catch (Exception e1) {
					return klass.getMethod(field.getName(), field.getType());
				}
			}
		} catch (Exception e) {
			throw Lang.makeThrow(NoSuchMethodException.class, "Fail to find setter for [%s]->[%s]",
					klass.getName(), field.getName());
		}
	}

	public Method getSetter(String fieldName, Class<?> paramType) throws NoSuchMethodException {
		try {
			String setterName = getSetterName(fieldName);
			try {
				return klass.getMethod(setterName, paramType);
			} catch (Exception e) {
				try {
					return klass.getMethod(fieldName, paramType);
				} catch (Exception e1) {
					Mirror<?> type = Mirror.me(paramType);
					for (Method method : klass.getMethods()) {
						if (method.getParameterTypes().length == 1)
							if (method.getName().equals(setterName)
									|| method.getName().equals(fieldName)) {
								if (null == paramType
										|| type.canCastToDirectly(method.getParameterTypes()[0]))
									return method;
							}
					}
					throw new Exception();
				}
			}
		} catch (Exception e) {
			throw Lang.makeThrow(NoSuchMethodException.class,
					"Fail to find setter for [%s]->[%s(%s)]", klass.getName(), fieldName, paramType
							.getName());
		}
	}

	public static String getSetterName(String fieldName) {
		return new StringBuilder("set").append(Strings.capitalize(fieldName)).toString();
	}

	public static String getBooleanSetterName(String fieldName) {
		if (fieldName.startsWith("is"))
			fieldName = fieldName.substring(2);
		return new StringBuilder("set").append(Strings.capitalize(fieldName)).toString();
	}

	public static String getGetterName(String fieldName) {
		return new StringBuilder("get").append(Strings.capitalize(fieldName)).toString();
	}

	public static String getBooleanGetterName(String fieldName) {
		if (fieldName.startsWith("is"))
			fieldName = fieldName.substring(2);
		return new StringBuilder("is").append(Strings.capitalize(fieldName)).toString();
	}

	public Method[] findSetters(String fieldName) {
		String mName = "set" + Strings.capitalize(fieldName);
		ArrayList<Method> ms = new ArrayList<Method>();
		for (Method m : this.klass.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) || m.getParameterTypes().length != 1)
				continue;
			if (m.getName().equals(mName)) {
				ms.add(m);
			}
		}
		return ms.toArray(new Method[ms.size()]);
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
		Map<String, Field> list = new HashMap<String, Field>();
		while (null != theClass && !(theClass == Object.class)) {
			Field[] fs = theClass.getDeclaredFields();
			for (int i = 0; i < fs.length; i++) {
				if (isIgnoredField(fs[i]))
					continue;
				if (list.containsKey(fs[i].getName()))
					continue;
				list.put(fs[i].getName(), fs[i]);
			}
			theClass = theClass.getSuperclass();
		}
		return list.values().toArray(new Field[list.size()]);
	}

	public Method[] getAllDeclaredMethods(Class<?> top) {
		Class<?> cc = klass;
		HashMap<String, Method> map = new HashMap<String, Method>();
		while (null != cc && !(cc == Object.class)) {
			Method[] fs = cc.getDeclaredMethods();
			for (int i = 0; i < fs.length; i++) {
				String key = fs[i].getName() + Mirror.getParamDescriptor(fs[i].getParameterTypes());
				map.put(key, fs[i]);
			}
			cc = cc.getSuperclass() == top ? null : cc.getSuperclass();
		}
		return map.values().toArray(new Method[map.size()]);
	}

	public Method[] getAllDeclaredMethodsWithoutTop() {
		return getAllDeclaredMethods(Object.class);
	}

	public Method[] getStaticMethods() {
		List<Method> list = new LinkedList<Method>();
		for (Method m : klass.getMethods()) {
			if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()))
				list.add(m);
		}
		return list.toArray(new Method[list.size()]);
	}

	private static RuntimeException makeSetValueException(Class<?> type, String name, Object value,
			Exception e) {
		return new FailToSetValueException(String.format(
				"Fail to set value [%s] to [%s]->[%s] because '%s'", value, type.getName(), name, e
						.getMessage()));
	}

	public void setValue(Object obj, Field field, Object value) throws FailToSetValueException {
		if (!field.isAccessible())
			field.setAccessible(true);
		Mirror<?> me = Mirror.me(field.getType());
		if (null != value)
			try {
				if (!Mirror.me(value.getClass()).canCastToDirectly(me.getType()))
					value = Castors.me().castTo(value, field.getType());
			} catch (FailToCastObjectException e) {
				throw makeSetValueException(obj.getClass(), field.getName(), value, e);
			}
		else {
			if (me.isNumber())
				value = 0;
			else if (me.isChar())
				value = (char) 0;
		}
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw makeSetValueException(obj.getClass(), field.getName(), value, e);
		}
	}

	public void setValue(Object obj, String fieldName, Object value) throws FailToSetValueException {
		try {
			this.getSetter(fieldName, value.getClass()).invoke(obj, value);
		} catch (Exception e) {
			try {
				Field field = this.getField(fieldName);
				setValue(obj, field, value);
			} catch (Exception e1) {
				throw makeSetValueException(obj.getClass(), fieldName, value, e1);
			}
		}
	}

	private static RuntimeException makeGetValueException(Class<?> type, String name) {
		return new FailToGetValueException(String.format("Fail to get value for [%s]->[%s]", type
				.getName(), name));
	}

	public Object getValue(Object obj, Field f) throws FailToGetValueException {
		try {
			if (!f.isAccessible())
				f.setAccessible(true);
			return f.get(obj);
		} catch (Exception e) {
			throw makeGetValueException(obj.getClass(), f.getName());
		}
	}

	public Object getValue(Object obj, String name) throws FailToGetValueException {
		try {
			return this.getGetter(name).invoke(obj);
		} catch (Exception e) {
			try {
				Field f = getField(name);
				return getValue(obj, f);
			} catch (NoSuchFieldException e1) {
				throw makeGetValueException(obj.getClass(), name);
			}
		}
	}

	public Class<T> getType() {
		return klass;
	}

	public Class<?>[] extractTypes() {
		return typeExtractor.extract(this);
	}

	public Class<?> getWrapperClass() {
		if (!klass.isPrimitive()) {
			if (this.isPrimitiveNumber() || this.is(Boolean.class) || this.is(Character.class))
				return klass;
			throw Lang.makeThrow("Class '%s' should be a primitive class", klass.getName());
		}
		if (is(int.class))
			return Integer.class;
		if (is(char.class))
			return Character.class;
		if (is(boolean.class))
			return Boolean.class;
		if (is(long.class))
			return Long.class;
		if (is(float.class))
			return Float.class;
		if (is(byte.class))
			return Byte.class;
		if (is(short.class))
			return Short.class;
		if (is(double.class))
			return Double.class;

		throw Lang.makeThrow("Class [%s] has no wrapper class!", klass.getName());
	}

	public Class<?> getWrapper() {
		if (klass.isPrimitive())
			return getWrapperClass();
		return klass;
	}

	public Class<?> getOuterClass() {
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

	public MirrorBorning<T> getBorning(Object... args) {
		return new MirrorBorning<T>(this, args);
	}

	public T born(Object... args) {
		return this.getBorning(args).born();
	}

	private static boolean doMatchMethodParamsType(Class<?>[] paramTypes, Class<?>[] methodArgTypes) {
		if (paramTypes.length == 0 && methodArgTypes.length == 0)
			return true;
		if (paramTypes.length == methodArgTypes.length) {
			for (int i = 0; i < paramTypes.length; i++)
				if (!Mirror.me(paramTypes[i]).canCastToDirectly((methodArgTypes[i])))
					return false;
			return true;
		} else if (paramTypes.length + 1 == methodArgTypes.length) {
			if (!methodArgTypes[paramTypes.length].isArray())
				return false;
			for (int i = 0; i < paramTypes.length; i++)
				if (!Mirror.me(paramTypes[i]).canCastToDirectly((methodArgTypes[i])))
					return false;
			return true;
		}
		return false;
	}

	public Invoking getInvoking(String methodName, Object... args) {
		return new Invoking(klass, methodName, args);
	}

	public Object invoke(Object obj, String methodName, Object... args) {
		return getInvoking(methodName, args).invoke(obj);
	}

	public Method findMethod(String name, Class<?>... paramTypes) throws NoSuchMethodException {
		try {
			return klass.getMethod(name, paramTypes);
		} catch (NoSuchMethodException e) {
			for (Method m : klass.getMethods()) {
				if (m.getName().equals(name))
					if (doMatchMethodParamsType(paramTypes, m.getParameterTypes()))
						return m;
			}
		}
		throw new NoSuchMethodException(String.format(
				"Fail to find Method %s->%s with params:\n%s", klass.getName(), name, Castors.me()
						.castToString(paramTypes)));
	}

	public Method findMethod(Class<?> returnType, Class<?>... paramTypes)
			throws NoSuchMethodException {
		for (Method m : klass.getMethods()) {
			if (returnType == m.getReturnType())
				if (paramTypes.length == m.getParameterTypes().length) {
					boolean noThisOne = false;
					for (int i = 0; i < paramTypes.length; i++) {
						if (paramTypes[i] != m.getParameterTypes()[i]) {
							noThisOne = true;
							break;
						}
					}
					if (!noThisOne)
						return m;
				}
		}
		throw new NoSuchMethodException(String.format(
				"Can not find method in [%s] with return type '%s' and arguemtns \n'%s'!", klass
						.getName(), returnType.getName(), Castors.me().castToString(paramTypes)));

	}

	public static MatchType matchParamTypes(Class<?>[] methodParamTypes, Object... args) {
		return matchParamTypes(methodParamTypes, evalToTypes(args));
	}

	public static Class<?>[] evalToTypes(Object... args) {
		Class<?>[] types = new Class[args.length];
		int i = 0;
		for (Object arg : args)
			types[i++] = null == arg ? Object.class : arg.getClass();
		return types;
	}

	public static Object evalArgToSameTypeRealArray(Object... args) {
		Object array = evalArgToRealArray(args);
		return array == args ? null : array;
	}

	public static Object evalArgToRealArray(Object... args) {
		if (null == args || args.length == 0)
			return null;
		if (null == args[0])
			return null;
		Object re = null;
		/*
		 * Check inside the arguments list, to see if all element is in same
		 * type
		 */
		Class<?> type = null;
		for (Object arg : args) {
			if (null == arg)
				break;
			if (null == type) {
				type = arg.getClass();
				continue;
			}
			if (arg.getClass() != type) {
				type = null;
				break;
			}
		}
		/*
		 * If all argument elements in same type, make a new Array by the Type
		 */
		if (type != null) {
			re = Array.newInstance(type, args.length);
			for (int i = 0; i < args.length; i++) {
				Array.set(re, i, args[i]);
			}
			return re;
		}
		return args;

	}

	public static MatchType matchParamTypes(Class<?>[] paramTypes, Class<?>[] argTypes) {
		int len = argTypes == null ? 0 : argTypes.length;
		if (len == 0 && paramTypes.length == 0)
			return MatchType.YES;
		if (paramTypes.length == len) {
			for (int i = 0; i < len; i++)
				if (!Mirror.me(argTypes[i]).canCastToDirectly((paramTypes[i])))
					return MatchType.NO;
			return MatchType.YES;
		} else if (len + 1 == paramTypes.length) {
			if (!paramTypes[len].isArray())
				return MatchType.NO;
			for (int i = 0; i < len; i++)
				if (!Mirror.me(argTypes[i]).canCastToDirectly((paramTypes[i])))
					return MatchType.NO;
			return MatchType.LACK;
		}
		return MatchType.NO;
	}

	// @SuppressWarnings("unchecked")
	// public T duplicate(T src) {
	// Method m;
	// try {
	// m = klass.getMethod("clone");
	// return (T) m.invoke(src);
	// } catch (Exception failToClone) {
	// try {
	// T obj = born();
	// Field[] fields = getFields();
	// for (Field field : fields) {
	// Object value = getValue(src, field);
	// setValue(obj, field, value);
	// }
	// return obj;
	//
	// } catch (Exception e) {
	// throw Lang.wrapThrow(e);
	// }
	// }
	// }

	public boolean is(Class<?> type) {
		if (null == type)
			return false;
		if (klass == type)
			return true;
		return false;
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

	public boolean isEnum() {
		return klass.isEnum();
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
		try {
			return Mirror.me(type).getWrapperClass() == klass;
		} catch (Exception e) {}
		return false;
	}

	public boolean canCastToDirectly(Class<?> type) {
		if (klass == type)
			return true;
		if (type.isAssignableFrom(klass))
			return true;
		if (klass.isPrimitive() && type.isPrimitive()) {
			if (this.isPrimitiveNumber() && Mirror.me(type).isPrimitiveNumber())
				return true;
		}
		try {
			return Mirror.me(type).getWrapperClass() == this.getWrapperClass();
		} catch (Exception e) {}
		return false;
	}

	public boolean isPrimitiveNumber() {
		return isInt() || isLong() || isFloat() || isDouble() || isByte() || isShort();
	}

	public boolean isNumber() {
		return Number.class.isAssignableFrom(klass) || klass.isPrimitive() && !is(boolean.class)
				&& !is(char.class);
	}

	public boolean isDateTimeLike() {
		return Calendar.class.isAssignableFrom(klass)
				|| java.util.Date.class.isAssignableFrom(klass)
				|| java.sql.Timestamp.class.isAssignableFrom(klass)
				|| java.sql.Date.class.isAssignableFrom(klass)
				|| java.sql.Time.class.isAssignableFrom(klass);
	}

	static Object[] blankArrayArg(Class<?>[] pts) {
		return (Object[]) Array.newInstance(pts[pts.length - 1].getComponentType(), 0);
	}

	public static Type[] getTypeParams(Class<?> klass) {
		Type superclass = klass.getGenericSuperclass();
		if (superclass instanceof Class<?>) {
			throw new RuntimeException("Missing type parameter.");
		}
		return ((ParameterizedType) superclass).getActualTypeArguments();
	}

	public static enum MatchType {
		YES, LACK, NO
	}

	public static String getPath(Class<?> klass) {
		return klass.getName().replace('.', '/');
	}

	public static String getParamDescriptor(Class<?>[] parameterTypes) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Class<?> pt : parameterTypes)
			sb.append(getTypeDescriptor(pt));
		sb.append(')');
		String s = sb.toString();
		return s;
	}

	public static String getMethodDescriptor(Method method) {
		return getParamDescriptor(method.getParameterTypes())
				+ getTypeDescriptor(method.getReturnType());
	}

	public static String getConstructorDescriptor(Constructor<?> c) {
		return getParamDescriptor(c.getParameterTypes()) + "V";
	}

	public static String getTypeDescriptor(Class<?> klass) {
		if (klass.isPrimitive()) {
			if (klass == void.class)
				return "V";
			else if (klass == int.class)
				return "I";
			else if (klass == long.class)
				return "J";
			else if (klass == byte.class)
				return "B";
			else if (klass == short.class)
				return "S";
			else if (klass == float.class)
				return "F";
			else if (klass == double.class)
				return "D";
			else if (klass == char.class)
				return "C";
			else
				/* if(klass == boolean.class) */
				return "Z";
		}
		StringBuilder sb = new StringBuilder();
		if (klass.isArray()) {
			return sb.append('[').append(getTypeDescriptor(klass.getComponentType())).toString();
		}
		return sb.append('L').append(Mirror.getPath(klass)).append(';').toString();
	}
}
