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
import java.util.Map.Entry;

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

/**
 * 这些帮助函数让 Java 的某些常用功能变得更简单
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Lang {

	public static ComboException comboThrow(Throwable... es) {
		ComboException ce = new ComboException();
		for (Throwable e : es)
			ce.add(e);
		return ce;
	}

	/**
	 * @return 一个未实现的运行时异常
	 */
	public static RuntimeException noImplement() {
		return new RuntimeException("Not implement yet!");
	}

	/**
	 * 根据格式化字符串，生成运行时异常
	 * 
	 * @param format
	 *            格式
	 * @param args
	 *            参数
	 * @return 运行时异常
	 */
	public static RuntimeException makeThrow(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}

	/**
	 * 根据格式化字符串，生成一个指定的异常。
	 * 
	 * @param classOfT
	 *            异常类型， 需要有一个字符串为参数的构造函数
	 * @param format
	 *            格式
	 * @param args
	 *            参数
	 * @return 异常对象
	 */
	public static <T extends Throwable> T makeThrow(Class<T> classOfT,
													String format,
													Object... args) {
		return Mirror.me(classOfT).born(String.format(format, args));
	}

	/**
	 * 将抛出对象包裹成运行时异常，并增加自己的描述
	 * 
	 * @param e
	 *            抛出对象
	 * @param fmt
	 *            格式
	 * @param args
	 *            参数
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e, String fmt, Object... args) {
		return new RuntimeException(String.format(fmt, args), e);
	}

	/**
	 * 用运行时异常包裹抛出对象，如果抛出对象本身就是运行时异常，则直接返回。
	 * <p>
	 * 如果是 InvocationTargetException，那么将其剥离，只包裹其 TargetException
	 * 
	 * @param e
	 *            抛出对象
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		if (e instanceof InvocationTargetException)
			return wrapThrow(((InvocationTargetException) e).getTargetException());
		return new RuntimeException(e);
	}

	/**
	 * 用一个指定可抛出类型来包裹一个抛出对象。这个指定的可抛出类型需要有一个构造函数 接受 Throwable 类型的对象
	 * 
	 * @param e
	 *            抛出对象
	 * @param wrapper
	 *            包裹类型
	 * @return 包裹后对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> T wrapThrow(Throwable e, Class<T> wrapper) {
		if (wrapper.isAssignableFrom(e.getClass()))
			return (T) e;
		return Mirror.me(wrapper).born(e);
	}

	/**
	 * 判断两个对象是否相等。 这个函数用处是:
	 * <ul>
	 * <li>可以容忍 null
	 * <li>可以容忍不同类型的 Number
	 * <li>对，数组，集合， Map 会深层比较
	 * </ul>
	 * 当然，如果你重写的 equals 方法会优先
	 * 
	 * @param a1
	 *            比较对象1
	 * @param a2
	 *            比较对象2
	 * @return 是否相等
	 */
	@SuppressWarnings("unchecked")
	public static boolean equals(Object a1, Object a2) {
		if (a1 == a2)
			return true;
		if (a1 == null || a2 == null)
			return false;
		if (a1.equals(a2))
			return true;
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
		return false;
	}

	/**
	 * 判断一个数组内是否包括某一个对象。 它的比较将通过 equals(Object,Object) 方法
	 * 
	 * @param array
	 *            数组
	 * @param ele
	 *            对象
	 * @return true 包含 false 不包含
	 */
	public static <T> boolean contains(T[] array, T ele) {
		if (null == array)
			return false;
		for (T e : array) {
			if (equals(e, ele))
				return true;
		}
		return false;
	}

	/**
	 * 从一个文本输入流读取所有内容，并将该流关闭
	 * 
	 * @param reader
	 *            文本输入流
	 * @return 输入流所有内容
	 */
	public static String readAll(Reader reader) {
		if (!(reader instanceof BufferedReader))
			reader = new BufferedReader(reader);
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

	/**
	 * 将一段字符串写入一个文本输出流，并将该流关闭
	 * 
	 * @param writer
	 *            文本输出流
	 * @param str
	 *            字符串
	 */
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

	/**
	 * 根据一段文本模拟出一个输入流对象
	 * 
	 * @param cs
	 *            文本
	 * @return 输出流对象
	 */
	public static InputStream ins(CharSequence cs) {
		return new StringInputStream(cs);
	}

	/**
	 * 根据一段文本模拟出一个文本输入流对象
	 * 
	 * @param cs
	 *            文本
	 * @return 文本输出流对象
	 */
	public static Reader inr(CharSequence cs) {
		return new StringReader(cs);
	}

	/**
	 * 根据一个 StringBuilder 模拟一个文本输出流对象
	 * 
	 * @param sb
	 *            StringBuilder 对象
	 * @return 文本输出流对象
	 */
	public static Writer opw(StringBuilder sb) {
		return new StringWriter(sb);
	}

	/**
	 * 根据一个 StringBuilder 模拟一个输出流对象
	 * 
	 * @param sb
	 *            StringBuilder 对象
	 * @return 输出流对象
	 */
	public static StringOutputStream ops(StringBuilder sb) {
		return new StringOutputStream(sb);
	}

	/**
	 * 较方便的创建一个数组，比如：
	 * 
	 * <pre>
	 * Pet[] pets = Lang.array(pet1, pet2, pet3);
	 * </pre>
	 * 
	 * @param eles
	 *            可变参数
	 * @return 数组对象
	 */
	public static <T> T[] array(T... eles) {
		return eles;
	}

	/**
	 * 将多个数组，合并成一个数组。如果这些数组为空，则返回 null
	 * 
	 * @param arys
	 *            数组对象
	 * @return 合并后的数组对象
	 */
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

	/**
	 * 将一个对象添加成为一个数组的第一个元素，从而生成一个新的数组
	 * 
	 * @param e
	 *            对象
	 * @param eles
	 *            数组
	 * @return 新数组
	 */
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

	/**
	 * 将一个对象添加成为一个数组的最后一个元素，从而生成一个新的数组
	 * 
	 * @param e
	 *            对象
	 * @param eles
	 *            数组
	 * @return 新数组
	 */
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

	/**
	 * 将一个数组转换成字符串
	 * <p>
	 * 所有的元素都被格式化字符串包裹。 这个格式话字符串只能有一个占位符， %s, %d 等，均可，请视你的数组内容而定
	 * 
	 * @param fmt
	 *            格式
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concatBy(String fmt, T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			sb.append(String.format(fmt, obj));
		return sb;
	}

	/**
	 * 将一个数组转换成字符串
	 * <p>
	 * 所有的元素都被格式化字符串包裹。 这个格式话字符串只能有一个占位符， %s, %d 等，均可，请视你的数组内容而定
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param ptn
	 *            格式
	 * @param c
	 *            分隔符
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concatBy(String ptn, Object c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T obj : objs)
			sb.append(String.format(ptn, obj)).append(c);
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	/**
	 * 将一个数组转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param c
	 *            分隔符
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concatBy(Object c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		if (null == objs)
			return sb;
		for (T obj : objs)
			sb.append(null == obj ? null : obj.toString()).append(c);
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb;
	}

	/**
	 * 将一个数组的部分元素转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param offset
	 *            开始元素的下标
	 * @param len
	 *            元素数量
	 * @param c
	 *            分隔符
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concatBy(int offset, int len, Object c, T[] objs) {
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

	/**
	 * 将一个数组所有元素拼合成一个字符串
	 * 
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T e : objs)
			sb.append(e.toString());
		return sb;
	}

	/**
	 * 将一个数组部分元素拼合成一个字符串
	 * 
	 * @param offset
	 *            开始元素的下标
	 * @param len
	 *            元素数量
	 * @param array
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(int offset, int len, T[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(array[i + offset].toString());
		}
		return sb;
	}

	/**
	 * 将一个或者多个数组填入一个集合。
	 * 
	 * @param <C>
	 *            集合类型
	 * @param <T>
	 *            数组元素类型
	 * @param coll
	 *            集合
	 * @param objss
	 *            数组 （数目可变）
	 * @return 集合对象
	 */
	public static <C extends Collection<T>, T> C fill(C coll, T[]... objss) {
		for (T[] objs : objss)
			for (T obj : objs)
				coll.add(obj);
		return coll;
	}

	/**
	 * 将一个集合变成 Map。
	 * 
	 * @param mapClass
	 *            Map 的类型
	 * @param coll
	 *            集合对象
	 * @param keyFieldName
	 *            采用集合中元素的哪个一个字段为键。
	 * @return Map 对象
	 */
	public static <T extends Map<Object, Object>> Map<?, ?> collection2map(	Class<T> mapClass,
																			Collection<?> coll,
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

	/**
	 * 将集合变成 ArrayList
	 * 
	 * @param coll
	 *            集合对象
	 * @return 列表对象
	 */
	public static <E> List<E> collection2list(Collection<E> coll) {
		return collection2list(coll, null);
	}

	/**
	 * 将集合编程变成指定类型的列表
	 * 
	 * @param coll
	 *            集合对象
	 * @param classOfList
	 *            列表类型
	 * @return 列表对象
	 */
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

	/**
	 * 将集合变成数组，数组的类型为集合的第一个元素的类型。如果集合为空，则返回 null
	 * 
	 * @param coll
	 *            集合对象
	 * @return 数组
	 */
	@SuppressWarnings("unchecked")
	public static <E> Object collection2array(Collection<E> coll) {
		if (null == coll || coll.size() == 0)
			return null;
		Class<E> eleType = (Class<E>) Lang.first(coll).getClass();
		return collection2array(coll, eleType);
	}

	/**
	 * 将集合变成指定类型的数组
	 * 
	 * @param coll
	 *            集合对象
	 * @param eleType
	 *            数组元素类型
	 * @return 数组
	 */
	public static Object collection2array(Collection<?> coll, Class<?> eleType) {
		if (null == coll)
			return null;
		Object re = Array.newInstance(eleType, coll.size());
		int i = 0;
		for (Iterator<?> it = coll.iterator(); it.hasNext();)
			Array.set(re, i++, it.next());
		return re;
	}

	/**
	 * 将一个数组变成 Map
	 * 
	 * @param mapClass
	 *            Map 的类型
	 * @param array
	 *            数组
	 * @param keyFieldName
	 *            采用集合中元素的哪个一个字段为键。
	 * @return Map 对象
	 */
	public static <T extends Map<Object, Object>> Map<?, ?> array2map(	Class<T> mapClass,
																		Object array,
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

	/**
	 * 将数组转换成另外一种类型的数组。将会采用 Castor 来深层转换数组元素
	 * 
	 * @param array
	 *            原始数组
	 * @param eleType
	 *            新数组的元素类型
	 * @return 新数组
	 * @throws FailToCastObjectException
	 * 
	 * @see org.nutz.castor.Castors
	 */
	public static Object array2array(Object array, Class<?> eleType)
			throws FailToCastObjectException {
		if (null == array)
			return null;
		int len = Array.getLength(array);
		Object re = Array.newInstance(eleType, len);
		for (int i = 0; i < len; i++) {
			Array.set(re, i, Castors.me().castTo(Array.get(array, i), eleType));
		}
		return re;
	}

	/**
	 * 将数组转换成Object[] 数组。将会采用 Castor 来深层转换数组元素
	 * 
	 * @param args
	 *            原始数组
	 * @param pts
	 *            新数组的元素类型
	 * @return 新数组
	 * @throws FailToCastObjectException
	 * 
	 * @see org.nutz.castor.Castors
	 */
	public static <T> Object[] array2ObjectArray(T[] args, Class<?>[] pts)
			throws FailToCastObjectException {
		Object[] newArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			newArgs[i] = Castors.me().castTo(args[i], pts[i]);
		}
		return newArgs;
	}

	/**
	 * 根据一个 Map，和给定的对象类型，创建一个新的 JAVA 对象
	 * 
	 * @param src
	 *            Map 对象
	 * @param toType
	 *            JAVA 对象类型
	 * @return JAVA 对象
	 * @throws FailToCastObjectException
	 */
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

	/**
	 * 根据一段字符串，生成一个 Map 对象。
	 * 
	 * @param str
	 *            参照 JSON 标准的字符串，但是可以没有前后的大括号
	 * @return Map 对象
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(String str) {
		if (null == str)
			return null;
		if (str.startsWith("{") && str.endsWith("}"))
			return (Map<String, Object>) Json.fromJson(str);
		return (Map<String, Object>) Json.fromJson("{" + str + "}");
	}

	/**
	 * 根据一段字符串，生成一个List 对象。
	 * 
	 * @param str
	 *            参照 JSON 标准的字符串，但是可以没有前后的中括号
	 * @return List 对象
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> list(String str) {
		if (null == str)
			return null;
		if (str.startsWith("[") && str.endsWith("]"))
			return (List<Object>) Json.fromJson(str);
		return (List<Object>) Json.fromJson("[" + str + "]");
	}

	/**
	 * 获得一个对象的长度。它可以接受:
	 * <ul>
	 * <li>null : 0
	 * <li>数组
	 * <li>集合
	 * <li>Map
	 * <li>一般 Java 对象。 返回 1
	 * </ul>
	 * 如果你想让你的 Java 对象返回不是 1 ， 请在对象中声明 length() 函数
	 * 
	 * @param obj
	 * @return 对象长度
	 */
	public static int length(Object obj) {
		if (null == obj)
			return 0;
		if (obj.getClass().isArray()) {
			return Array.getLength(obj);
		} else if (obj instanceof Collection<?>) {
			return ((Collection<?>) obj).size();
		} else if (obj instanceof Map<?, ?>) {
			return ((Map<?, ?>) obj).size();
		}
		try {
			return (Integer) Mirror.me(obj.getClass()).invoke(obj, "length");
		} catch (Exception e) {}
		return 1;
	}

	/**
	 * 取得第一个对象，无论是 数组，集合还是 Map。如果是一个一般 JAVA 对象，则返回自身
	 * 
	 * @param obj
	 *            任意对象
	 * @return 第一个代表对象
	 */
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

	/**
	 * 获取集合中的第一个元素，如果集合为空，返回 null
	 * 
	 * @param coll
	 *            集合
	 * @return 第一个元素
	 */
	public static <T> T first(Collection<T> coll) {
		if (null == coll || coll.size() == 0)
			return null;
		return coll.iterator().next();
	}

	/**
	 * 获得表中的第一个名值对
	 * 
	 * @param map
	 *            表
	 * @return 第一个名值对
	 */
	public static <K, V> Entry<K, V> first(Map<K, V> map) {
		if (null == map || map.size() == 0)
			return null;
		return map.entrySet().iterator().next();
	}

	/**
	 * 打断 each 循环
	 */
	public static void Break() throws ExitLoop {
		throw new ExitLoop();
	}

	/**
	 * 用回调的方式，遍历一个对象，可以支持遍历
	 * <ul>
	 * <li>数组
	 * <li>集合
	 * <li>Map
	 * <li>单一元素
	 * </ul>
	 * 
	 * @param obj
	 *            对象
	 * @param callback
	 *            回调
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> void each(Object obj, Each<T> callback) {
		if (null == obj || null == callback)
			return;
		try {
			Class<T>[] typeParams = (Class<T>[]) Mirror.getTypeParams(callback.getClass());
			Class<T> eType = null;
			if (typeParams != null && typeParams.length > 0)
				eType = typeParams[0];
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
				Map map = (Map) obj;
				int len = map.size();
				int i = 0;
				if (null != eType && eType.isAssignableFrom(Entry.class)) {
					for (Object v : map.entrySet())
						try {
							callback.invoke(i++, (T) v, len);
						} catch (ExitLoop e) {
							break;
						}

				} else {
					for (Object v : map.entrySet())
						try {
							callback.invoke(i++, (T) ((Entry) v).getValue(), len);
						} catch (ExitLoop e) {
							break;
						}
				}
			} else
				try {
					callback.invoke(0, (T) obj, 1);
				} catch (ExitLoop e) {}
		} catch (LoopException e) {
			throw Lang.wrapThrow(e.getCause());
		}
	}

	/**
	 * 将一个抛出对象的异常堆栈，显示成一个字符串
	 * 
	 * @param e
	 *            抛出对象
	 * @return 异常堆栈文本
	 */
	public static String getStackTrace(Throwable e) {
		StringBuilder sb = new StringBuilder();
		StringOutputStream sbo = new StringOutputStream(sb);
		PrintStream ps = new PrintStream(sbo);
		e.printStackTrace(ps);
		ps.flush();
		return sbo.getStringBuilder().toString();
	}

	/**
	 * 将字符串解析成 boolean 值，支持更多的字符串
	 * <ul>
	 * <li>1 | 0
	 * <li>yes | no
	 * <li>on | off
	 * <li>true | false
	 * </ul>
	 * 
	 * @param s
	 * @return 布尔值
	 */
	public static boolean parseBoolean(String s) {
		if (null == s)
			return false;
		if (s.length() == 0)
			return false;
		if (s.length() > 5)
			return true;
		if ("0".equals(s))
			return false;
		s = s.toLowerCase();
		if ("false".equals(s) || "off".equals(s) || "no".equals(s))
			return false;
		return true;
	}

	/**
	 * 帮你快速获得一个 DocumentBuilder，方便 XML 解析。
	 * 
	 * @return 一个 DocumentBuilder 对象
	 * @throws ParserConfigurationException
	 */
	public static DocumentBuilder xmls() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder();
	}
}
