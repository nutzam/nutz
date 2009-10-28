package org.nutz.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.stream.StringOutputStream;
import org.nutz.lang.stream.StringReader;
import org.nutz.lang.stream.StringWriter;

public abstract class Lang {

	public static final String NULL = "";

	public static RuntimeException makeThrow(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}

	public static <T extends Throwable> T makeThrow(Class<T> classOfT, String format, Object... args) {
		return Mirror.me(classOfT).born(String.format(format, args));
	}

	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		if (e instanceof InvocationTargetException)
			return wrapThrow(((InvocationTargetException) e).getTargetException());
		return new RuntimeException(e);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Throwable> T wrapThrow(Throwable e, Class<T> wrapper) {
		if (wrapper.isAssignableFrom(e.getClass()))
			return (T) e;
		return Mirror.me(wrapper).born(e);
	}

	@SuppressWarnings("unchecked")
	public static boolean equals(Object a1, Object a2) {
		if (a1 == a2)
			return true;
		if (a1 == null || a2 == null)
			return false;
		if (a1 instanceof Number) {
			if (!(a2 instanceof Number))
				return false;
			return a1.toString().equals(a2.toString());
		} else if (a1 instanceof Map && a2 instanceof Map) {
			Map<?, ?> m1 = (Map<?, ?>) a1;
			Map<?, ?> m2 = (Map<?, ?>) a2;
			if (m1.size() != m2.size())
				return false;
			for (Iterator<?> it = m1.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				if (!m2.containsKey(key))
					return false;
				Object v1 = m1.get(key);
				Object v2 = m2.get(key);
				if (!equals(v1, v2))
					return false;
			}
			return true;
		} else if (a1.getClass().isArray()) {
			if (a2.getClass().isArray()) {
				int len = Array.getLength(a1);
				if (len != Array.getLength(a2))
					return false;
				for (int i = 0; i < len; i++) {
					if (!equals(Array.get(a1, i), Array.get(a2, i)))
						return false;
				}
				return true;
			} else if (a2 instanceof List) {
				return equals(a1, Lang.collection2array((List<Object>) a2, Object.class));
			}
			return false;
		} else if (a1 instanceof List) {
			if (a2 instanceof List) {
				List<?> l1 = (List<?>) a1;
				List<?> l2 = (List<?>) a2;
				if (l1.size() != l2.size())
					return false;
				int i = 0;
				for (Iterator<?> it = l1.iterator(); it.hasNext();) {
					if (!equals(it.next(), l2.get(i++)))
						return false;
				}
				return true;
			} else if (a2.getClass().isArray()) {
				return equals(Lang.collection2array((List<Object>) a1, Object.class), a2);
			}
			return false;
		} else if (a1 instanceof Collection && a2 instanceof Collection) {
			Collection<?> c1 = (Collection<?>) a1;
			Collection<?> c2 = (Collection<?>) a2;
			if (c1.size() != c2.size())
				return false;
			return c1.containsAll(c2) && c2.containsAll(c1);
		}
		return a1.equals(a2);
	}

	public static <T> boolean contains(T[] array, T ele) {
		if (null == array)
			return false;
		for (T e : array) {
			if (equals(e, ele))
				return true;
		}
		return false;
	}

	public static String readAll(Reader reader) {
		try {
			StringBuilder sb = new StringBuilder();
			int c;
			while (-1 != (c = reader.read()))
				sb.append((char) c);
			return sb.toString();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		} finally {
			Streams.safeClose(reader);
		}
	}

	public static String bufferAll(Reader reader) {
		if (reader instanceof BufferedReader)
			return readAll(reader);
		return readAll(new BufferedReader(reader));
	}

	public static void writeAll(Writer writer, String str) {
		try {
			writer.write(str);
			writer.flush();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		} finally {
			Streams.safeClose(writer);
		}
	}

	public static InputStream ins(CharSequence cs) {
		return new StringInputStream(cs);
	}

	public static Reader inr(CharSequence cs) {
		return new StringReader(cs);
	}

	public static Writer opw(StringBuilder sb) {
		return new StringWriter(sb);
	}

	public static StringOutputStream ops(StringBuilder sb) {
		return new StringOutputStream(sb);
	}

	public static <T> T[] array(T... eles) {
		return eles;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] merge(T[]... arys) {
		Queue<T> list = new LinkedList<T>();
		for (T[] ary : arys)
			if (null != ary)
				for (T e : ary)
					if (null != e)
						list.add(e);
		if (list.size() == 0)
			return null;
		Class<T> type = (Class<T>) list.peek().getClass();
		return list.toArray((T[]) Array.newInstance(type, list.size()));
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

	public static <T> StringBuilder concatBy(String ptn, T[] o) {
		StringBuilder sb = new StringBuilder();
		for (T obj : o)
			sb.append(String.format(ptn, obj));
		return sb;
	}

	public static <T> StringBuilder concatBy(String ptn, char c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			sb.append(String.format(ptn, obj)).append(c);
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concatBy(char c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		if (null == objs)
			return sb;
		for (T obj : objs)
			sb.append(null == obj ? null : obj.toString()).append(c);
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concatBy(int offset, int len, char c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		if (null == objs)
			return sb;
		for (int i = 0; i < len; i++) {
			Object obj = objs[i + offset];
			sb.append(null == obj ? null : obj.toString()).append(c);
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	public static <T> StringBuilder concat(T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T e : objs)
			sb.append(e.toString());
		return sb;
	}

	public static <T> StringBuilder concat(int offset, int len, T[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(array[i + offset].toString());
		}
		return sb;
	}

	public static <C extends Collection<T>, T> C fill(C coll, T[]... objss) {
		for (T[] objs : objss)
			for (T obj : objs)
				coll.add(obj);
		return coll;
	}

	public static <T extends Map<Object, Object>> Map<?, ?> collection2map(Class<T> mapClass, Collection<?> coll,
			String keyFieldName) {
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

	public static <E> List<E> collection2list(Collection<E> coll) {
		return collection2list(coll, null);
	}

	public static <E> List<E> collection2list(Collection<E> coll, Class<List<E>> classOfList) {
		if (coll instanceof List<?>)
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
	public static <E> E[] collection2array(Collection<E> coll) {
		if (null == coll || coll.size() == 0)
			return null;
		Class<E> eleType = (Class<E>) Lang.first(coll).getClass();
		return collection2array(coll, eleType);
	}

	@SuppressWarnings("unchecked")
	public static <E> E[] collection2array(Collection<E> coll, Class<E> classOfE) {
		if (null == coll)
			return null;
		E[] re = (E[]) Array.newInstance(classOfE, coll.size());
		int i = 0;
		for (Iterator<E> it = coll.iterator(); it.hasNext();)
			Array.set(re, i++, it.next());
		return re;
	}

	public static <T extends Map<Object, Object>> Map<?, ?> array2map(Class<T> mapClass, Object array,
			String keyFieldName) {
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
	public static <T> T[] array2array(Object array, Class<T> eleType) throws FailToCastObjectException {
		if (null == array)
			return null;
		T[] re = (T[]) Array.newInstance(eleType, Array.getLength(array));
		for (int i = 0; i < re.length; i++) {
			Array.set(re, i, Castors.me().castTo(Array.get(array, i), eleType));
		}
		return re;
	}

	public static <T> Object[] array2ObjectArray(T[] args, Class<?>[] pts) throws FailToCastObjectException {
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

	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(String str) {
		if (null == str)
			return null;
		return (Map<String, Object>) Json.fromJson(str);
	}

	@SuppressWarnings("unchecked")
	public static List<Object> list(String str) {
		if (null == str)
			return null;
		if (str.startsWith("[") && str.endsWith("]"))
			return (List<Object>) Json.fromJson(str);
		return (List<Object>) Json.fromJson("[" + str + "]");
	}

	public static int lenght(Object obj) {
		if (null == obj)
			return 0;
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).size();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).size();
		}
		return 1;
	}

	public static int maxLength(Collection<? extends CharSequence> coll) {
		int re = 0;
		if (null != coll)
			for (CharSequence s : coll)
				if (null != s)
					re = Math.max(re, s.length());
		return re;
	}

	public static <T extends CharSequence> int maxLength(T[] array) {
		int re = 0;
		if (null != array)
			for (CharSequence s : array)
				if (null != s)
					re = Math.max(re, s.length());
		return re;
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

	public static String getStackTrace(Throwable e) {
		StringBuilder sb = new StringBuilder();
		StringOutputStream sbo = new StringOutputStream(sb);
		PrintStream ps = new PrintStream(sbo);
		e.printStackTrace(ps);
		ps.flush();
		return sbo.getStringBuilder().toString();
	}

	public static boolean parseBoolean(String s) {
		if (s.equals("1"))
			return true;
		return Boolean.parseBoolean(s);
	}

	public static DocumentBuilder xmls() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
}
