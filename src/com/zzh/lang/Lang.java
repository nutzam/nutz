package com.zzh.lang;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.stream.CharInputStream;
import com.zzh.lang.stream.CharOutputStream;

public class Lang {

	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		return new RuntimeException(e);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> T wrapThrow(Throwable e, Class<T> wrapper) {
		if (wrapper.isAssignableFrom(e.getClass()))
			return (T) e;
		return Mirror.me(wrapper).born(e);
	}

	public static boolean equals(Object a1, Object a2) {
		if (a1 == a2)
			return true;
		if (a1 == null || a2 == null)
			return false;
		return a1.equals(a2);
	}

	public static InputStream ins(CharSequence cs) {
		return new CharInputStream(cs);
	}

	public static CharOutputStream ops() {
		return new CharOutputStream(new StringBuilder());
	}

	public static <T> T[] array(T... ele) {
		return ele;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] join(T e, T[] eles) {
		try {
			if (null == eles || eles.length == 0) {
				T[] arr = (T[]) Array.newInstance(e.getClass(), 1);
				arr[0] = e;
				return arr;
			}
			T[] arr = (T[]) Array.newInstance(e.getClass(), eles.length + 1);
			arr[0] = e;
			for (int i = 0; i < eles.length; i++) {
				arr[i + 1] = eles[i];
			}
			return arr;
		} catch (NegativeArraySizeException e1) {
			throw Lang.wrapThrow(e1);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] join(T[]... arrs) {
		List<T> list = new LinkedList<T>();
		Class<?> type = null;
		for (T[] arr : arrs)
			for (T e : arr) {
				if (null == type)
					type = e.getClass();
				list.add(e);
			}
		T[] arr = (T[]) Array.newInstance(type, list.size());
		return list.toArray(arr);
	}

	public static <T> StringBuilder concatBy(String ptn, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(String.format(ptn, t));
		return sb;
	}

	public static <T> StringBuilder concatBy(String ptn, char c, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(String.format(ptn, t)).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concatBy(char c, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T t : o)
			sb.append(t.toString()).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concat(T... o) {
		return concatBy(',', o);
	}

	public static <C extends Collection<T>, T> C fill(C coll, T[]... arr) {
		for (T[] es : arr)
			for (T e : es)
				coll.add(e);
		return coll;
	}

	@SuppressWarnings("unchecked")
	public static Map collection2map(Collection coll, String keyFieldName)
			throws NoSuchFieldException {
		if (null == coll)
			return null;
		Map map = new HashMap();
		if (coll.size() > 0) {
			Iterator<?> it = coll.iterator();
			Object obj = it.next();
			Mirror mirror = Mirror.me(obj.getClass());
			Field keyField = mirror.getField(keyFieldName);
			Object key = mirror.getValue(obj, keyField);
			map.put(key, obj);
			for (; it.hasNext();) {
				obj = it.next();
				key = mirror.getValue(obj, keyField);
				map.put(key, obj);
			}
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static Map array2map(Object array, String keyFieldName) throws NoSuchFieldException {
		if (null == array)
			return null;
		Map map = new HashMap();
		int len = Array.getLength(array);
		if (len > 0) {
			Object obj = Array.get(array, 0);
			Mirror mirror = Mirror.me(obj.getClass());
			Field keyField = mirror.getField(keyFieldName);
			for (int i = 0; i < len; i++) {
				obj = Array.get(array, i);
				Object key = mirror.getValue(obj, keyField);
				map.put(key, obj);
			}
		}
		return map;
	}

	public static <T> T[] array2array(Object array, Class<T[]> arrayType)
			throws FailToCastObjectException {
		return array2array(array, arrayType, Castors.me());
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] array2array(Object array, Class<T[]> arrayType, Castors castors)
			throws FailToCastObjectException {
		if (null == array)
			return null;
		Class<T> ct = (Class<T>) arrayType.getComponentType();
		T[] re = (T[]) Array.newInstance(ct, Array.getLength(array));
		for (int i = 0; i < re.length; i++) {
			Array.set(re, i, castors.castTo(Array.get(array, i), ct));
		}
		return re;
	}

	public static Object map2Object(Map<?, ?> src, Class<?> toType)
			throws FailToCastObjectException {
		return Lang.map2Object(src, toType, Castors.me());
	}

	public static Object map2Object(Map<?, ?> src, Class<?> toType, Castors castors)
			throws FailToCastObjectException {
		Mirror<?> mirror = Mirror.me(toType);
		Object obj = mirror.born();
		for (Field field : mirror.getFields()) {
			Object v = src.get(field.getName());
			Object vv = castors.castTo(v, field.getType());
			mirror.setValue(obj, field, vv);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> void each(Object obj, Each<T> callback) {
		if (null == obj || null == callback)
			return;
		if (obj.getClass().isArray()) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++)
				try {
					callback.invoke(i, (T) Array.get(obj, i), len);
				} catch (ExitLoop e) {
					break;
				}
		} else if (obj instanceof Collection) {
			int len = ((Collection) obj).size();
			int i = 0;
			for (Iterator<T> it = ((Collection) obj).iterator(); it.hasNext();)
				try {
					callback.invoke(i++, it.next(), len);
				} catch (ExitLoop e) {
					break;
				}
		} else if (obj instanceof CharSequence) {
			int len = ((CharSequence) obj).length();
			for (int i = 0; i < len; i++)
				try {
					callback.invoke(i, (T) Character.valueOf(((CharSequence) obj).charAt(i)), len);
				} catch (ExitLoop e) {
					break;
				}
		} else if (obj instanceof InputStream) {
			try {
				int len = ((InputStream) obj).available();
				int i = 0;
				int c;
				while ((c = ((InputStream) obj).read()) != -1) {
					try {
						callback.invoke(i++, (T) Character.valueOf((char) c), len);
					} catch (ExitLoop e) {
						break;
					}
				}
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		} else
			throw new RuntimeException("Lang don't know how to iterate the obj : " + obj.toString());
	}
}
