package com.zzh.lang;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

	public static final String NULL = "";

	public static RuntimeException makeThrow(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}

	public static <T extends Throwable> T makeThrow(Class<T> classOfT, String format,
			Object... args) {
		return Mirror.me(classOfT).born(String.format(format, args));
	}

	public static RuntimeException wrapThrow(Throwable e) {
		return wrapThrow(e, RuntimeException.class);
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

	public static Reader inr(CharSequence cs) {
		return new InputStreamReader(new CharInputStream(cs));
	}

	public static Writer opw(StringBuilder sb) {
		return new OutputStreamWriter(new CharOutputStream(sb));
	}

	public static CharOutputStream ops() {
		return new CharOutputStream(new StringBuilder());
	}

	public static <T> T[] array(T... ele) {
		return ele;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] arrayFirst(T e, T[] eles) {
		try {
			if (null == eles || eles.length == 0) {
				T[] arr = (T[]) Array.newInstance(e.getClass(), 1);
				arr[0] = e;
				return arr;
			}
			T[] arr = (T[]) Array.newInstance(eles.getClass().getComponentType(), eles.length + 1);
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
	public static <T> T[] arrayLast(T[] eles, T e) {
		try {
			if (null == eles || eles.length == 0) {
				T[] arr = (T[]) Array.newInstance(e.getClass(), 1);
				arr[0] = e;
				return arr;
			}
			T[] arr = (T[]) Array.newInstance(eles.getClass().getComponentType(), eles.length + 1);
			for (int i = 0; i < eles.length; i++) {
				arr[i] = eles[i];
			}
			arr[eles.length] = e;
			return arr;
		} catch (NegativeArraySizeException e1) {
			throw Lang.wrapThrow(e1);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] flat(T[]... arrs) {
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

	public static List<?> flat(Object obj) {
		return flatTo(obj, LinkedList.class);
	}

	public static <T extends Collection<?>> T flatTo(Object obj, Class<T> type) {
		try {
			final List<Object> re = new LinkedList<Object>();
			Lang.each(obj, new Each<Object>() {
				public void invoke(int i, Object obj, int length) throws ExitLoop {
					List<?> list = flat(obj);
					re.addAll(list);
				}
			});
			return Castors.me().castTo(re, type);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static <T> StringBuilder concatBy(String ptn, T... o) {
		StringBuilder sb = new StringBuilder();
		for (T obj : o)
			sb.append(String.format(ptn, obj));
		return sb;
	}

	public static <T> StringBuilder concatBy(String ptn, char c, T... objs) {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			sb.append(String.format(ptn, obj)).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concatBy(char c, T... objs) {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			sb.append(null == obj ? null : obj.toString()).append(c);
		sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concat(T... objs) {
		return concatBy(',', objs);
	}

	public static <C extends Collection<T>, T> C fill(C coll, T[]... objss) {
		for (T[] objs : objss)
			for (T obj : objs)
				coll.add(obj);
		return coll;
	}

	public static <T extends Map<Object, Object>> Map<?, ?> collection2map(Class<T> mapClass,
			Collection<?> coll, String keyFieldName) {
		if (null == coll)
			return null;
		Map<Object, Object> map = createMap(mapClass);
		if (coll.size() > 0) {
			Iterator<?> it = coll.iterator();
			Object obj = it.next();
			Mirror<?> mirror = Mirror.me(obj.getClass());
			Object key = mirror.getValue(obj, keyFieldName);
			map.put(key, obj);
			for (; it.hasNext();) {
				obj = it.next();
				key = mirror.getValue(obj, keyFieldName);
				map.put(key, obj);
			}
		}
		return map;
	}

	public static <E> List<E> collection2list(Collection<E> coll, Class<List<E>> classOfList) {
		if (coll instanceof List)
			return (List<E>) coll;
		List<E> list;
		try {
			list = (null == classOfList ? new ArrayList<E>(coll.size()) : classOfList.newInstance());
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		for (Iterator<E> it = coll.iterator(); it.hasNext();) {
			list.add(it.next());
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <E> E[] collection2array(Collection<E> coll, Class<E> classOfE) {
		E[] re = (E[]) Array.newInstance(classOfE, coll.size());
		int i = 0;
		for (Iterator<E> it = coll.iterator(); it.hasNext();)
			Array.set(re, i++, it.next());
		return re;
	}

	public static <T extends Map<Object, Object>> Map<?, ?> array2map(Class<T> mapClass,
			Object array, String keyFieldName) {
		if (null == array)
			return null;
		Map<Object, Object> map = createMap(mapClass);
		int len = Array.getLength(array);
		if (len > 0) {
			Object obj = Array.get(array, 0);
			Mirror<?> mirror = Mirror.me(obj.getClass());
			for (int i = 0; i < len; i++) {
				obj = Array.get(array, i);
				Object key = mirror.getValue(obj, keyFieldName);
				map.put(key, obj);
			}
		}
		return map;
	}

	private static <T extends Map<Object, Object>> Map<Object, Object> createMap(Class<T> mapClass) {
		Map<Object, Object> map;
		try {
			map = mapClass.newInstance();
		} catch (Exception e) {
			map = new HashMap<Object, Object>();
		}
		if (!mapClass.isAssignableFrom(map.getClass())) {
			throw Lang.makeThrow("Fail to create map [%s]", mapClass.getName());
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] array2array(Object array, Class<T[]> arrayType)
			throws FailToCastObjectException {
		if (null == array)
			return null;
		Class<T> ct = (Class<T>) arrayType.getComponentType();
		T[] re = (T[]) Array.newInstance(ct, Array.getLength(array));
		for (int i = 0; i < re.length; i++) {
			Array.set(re, i, Castors.me().castTo(Array.get(array, i), ct));
		}
		return re;
	}

	public static <T> Object[] array2ObjectArray(T[] args, Class<?>[] pts)
			throws FailToCastObjectException {
		Object[] newArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			newArgs[i] = Castors.me().castTo(args[i], pts[i]);
		}
		return newArgs;
	}

	public static <T> T map2Object(Map<?, ?> src, Class<T> toType) throws FailToCastObjectException {
		Mirror<T> mirror = Mirror.me(toType);
		T obj = mirror.born();
		for (Field field : mirror.getFields()) {
			Object v = src.get(field.getName());
			Object vv = Castors.me().castTo(v, field.getType());
			mirror.setValue(obj, field, vv);
		}
		return obj;
	}

	public static int lenght(Object obj) {
		if (null == obj)
			return 0;
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else if (obj instanceof Collection) {
			return ((Collection<?>) obj).size();
		} else if (obj instanceof Map) {
			return ((Map<?, ?>) obj).size();
		}
		return 1;
	}

	public static Object first(Object obj) {
		final Object[] re = new Object[1];
		each(obj, new Each<Object>() {
			public void invoke(int i, Object obj, int length) throws ExitLoop {
				re[0] = obj;
				Lang.Break();
			}
		});
		return re[0];
	}

	public static <T> T first(Collection<T> coll) {
		if (null == coll || coll.size() == 0)
			return null;
		return coll.iterator().next();
	}

	public static <T> T first(Map<?, T> map) {
		if (null == map || map.size() == 0)
			return null;
		return map.values().iterator().next();
	}

	public static void Break() throws ExitLoop {
		throw new ExitLoop();
	}

	@SuppressWarnings("unchecked")
	public static <T> void each(Object obj, Each<T> callback) {
		if (null == obj || null == callback)
			return;
		try {
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
			} else if (obj instanceof Map) {
				int len = ((Map) obj).size();
				int i = 0;
				for (Iterator<T> it = ((Map) obj).values().iterator(); it.hasNext();)
					try {
						callback.invoke(i++, it.next(), len);
					} catch (ExitLoop e) {
						break;
					}
			} else
				try {
					callback.invoke(0, (T) obj, 1);
				} catch (ExitLoop e) {}
		} catch (LoopException e) {
			throw Lang.wrapThrow(e.getCause());
		}
	}

}
