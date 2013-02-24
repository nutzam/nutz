package org.nutz.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.stream.StringInputStream;
import org.nutz.lang.stream.StringOutputStream;
import org.nutz.lang.stream.StringWriter;
import org.nutz.lang.util.ClassTools;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.SimpleContext;

/**
 * 这些帮助函数让 Java 的某些常用功能变得更简单
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author bonyfish(mc02cxj@gmail.com)
 */
public abstract class Lang {

    public static ComboException comboThrow(Throwable... es) {
        ComboException ce = new ComboException();
        for (Throwable e : es)
            ce.add(e);
        return ce;
    }

    /**
     * 生成一个未实现的运行时异常
     * 
     * @return 一个未实现的运行时异常
     */
    public static RuntimeException noImplement() {
        return new RuntimeException("Not implement yet!");
    }

    /**
     * 生成一个不可能的运行时异常
     * 
     * @return 一个不可能的运行时异常
     */
    public static RuntimeException impossible() {
        return new RuntimeException("r u kidding me?! It is impossible!");
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

    public static Throwable unwrapThrow(Throwable e) {
        if (e == null)
            return null;
        if (e instanceof InvocationTargetException) {
            InvocationTargetException itE = (InvocationTargetException) e;
            if (itE.getTargetException() != null)
                return unwrapThrow(itE.getTargetException());
        }
        if (e instanceof RuntimeException && e.getCause() != null)
            return unwrapThrow(e.getCause());
        return e;
    }

    /**
     * 判断两个对象是否相等。 这个函数用处是:
     * <ul>
     * <li>可以容忍 null
     * <li>可以容忍不同类型的 Number
     * <li>对数组，集合， Map 会深层比较
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

        Mirror<?> mr1 = Mirror.me(a1);

        if (mr1.isStringLike()) {
            return a1.toString().equals(a2.toString());
        }
        if (mr1.isDateTimeLike()) {
            return a1.equals(a2);
        }
        if (mr1.isNumber()) {
            return a2 instanceof Number && a1.toString().equals(a2.toString());
        }

        if (!a1.getClass().isAssignableFrom(a2.getClass())
            && !a2.getClass().isAssignableFrom(a1.getClass()))
            return false;

        if (a1 instanceof Map && a2 instanceof Map) {
            Map<?, ?> m1 = (Map<?, ?>) a1;
            Map<?, ?> m2 = (Map<?, ?>) a2;
            if (m1.size() != m2.size())
                return false;
            for (Entry<?, ?> e : m1.entrySet()) {
                Object key = e.getKey();
                if (!m2.containsKey(key) || !equals(m1.get(key), m2.get(key)))
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

            char[] data = new char[64];
            int len;
            while (true) {
                if ((len = reader.read(data)) == -1)
                    break;
                sb.append(data, 0, len);
            }
            return sb.toString();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
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
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
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
        return new StringReader(cs.toString());
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
     * 判断一个对象是否为空。它支持如下对象类型：
     * <ul>
     * <li>null : 一定为空
     * <li>数组
     * <li>集合
     * <li>Map
     * <li>其他对象 : 一定不为空
     * </ul>
     * 
     * @param obj
     *            任意对象
     * @return 是否为空
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj.getClass().isArray())
            return Array.getLength(obj) == 0;
        if (obj instanceof Collection<?>)
            return ((Collection<?>) obj).isEmpty();
        if (obj instanceof Map<?, ?>)
            return ((Map<?, ?>) obj).isEmpty();
        return false;
    }

    /**
     * 判断一个数组是否是空数组
     * 
     * @param ary
     *            数组
     * @return null 或者空数组都为 true 否则为 false
     */
    public static <T> boolean isEmptyArray(T[] ary) {
        return null == ary || ary.length == 0;
    }

    /**
     * 较方便的创建一个列表，比如：
     * 
     * <pre>
     * List&lt;Pet&gt; pets = Lang.list(pet1, pet2, pet3);
     * </pre>
     * 
     * 注，这里的 List，是 ArrayList 的实例
     * 
     * @param eles
     *            可变参数
     * @return 列表对象
     */
    public static <T> ArrayList<T> list(T... eles) {
        ArrayList<T> list = new ArrayList<T>(eles.length);
        for (T ele : eles)
            list.add(ele);
        return list;
    }

    /**
     * 创建一个 Hash 集合
     * 
     * @param eles
     *            可变参数
     * @return 集合对象
     */
    public static <T> Set<T> set(T... eles) {
        Set<T> set = new HashSet<T>();
        for (T ele : eles)
            set.add(ele);
        return set;
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
        if (list.isEmpty())
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
        }
        catch (NegativeArraySizeException e1) {
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
        }
        catch (NegativeArraySizeException e1) {
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
    public static <T> StringBuilder concat(Object c, T[] objs) {
        StringBuilder sb = new StringBuilder();
        if (null == objs || 0 == objs.length)
            return sb;

        sb.append(objs[0]);
        for (int i = 1; i < objs.length; i++)
            sb.append(c).append(objs[i]);

        return sb;
    }

    /**
     * 将一个长整型数组转换成字符串
     * <p>
     * 每个元素之间，都会用一个给定的字符分隔
     * 
     * @param c
     *            分隔符
     * @param vals
     *            数组
     * @return 拼合后的字符串
     */
    public static StringBuilder concat(Object c, long[] vals) {
        StringBuilder sb = new StringBuilder();
        if (null == vals || 0 == vals.length)
            return sb;

        sb.append(vals[0]);
        for (int i = 1; i < vals.length; i++)
            sb.append(c).append(vals[i]);

        return sb;
    }

    /**
     * 将一个整型数组转换成字符串
     * <p>
     * 每个元素之间，都会用一个给定的字符分隔
     * 
     * @param c
     *            分隔符
     * @param vals
     *            数组
     * @return 拼合后的字符串
     */
    public static StringBuilder concat(Object c, int[] vals) {
        StringBuilder sb = new StringBuilder();
        if (null == vals || 0 == vals.length)
            return sb;

        sb.append(vals[0]);
        for (int i = 1; i < vals.length; i++)
            sb.append(c).append(vals[i]);

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
    public static <T> StringBuilder concat(int offset, int len, Object c, T[] objs) {
        StringBuilder sb = new StringBuilder();
        if (null == objs || len < 0 || 0 == objs.length)
            return sb;

        if (offset < objs.length) {
            sb.append(objs[offset]);
            for (int i = 1; i < len && i + offset < objs.length; i++) {
                sb.append(c).append(objs[i + offset]);
            }
        }
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
     * 将一个集合转换成字符串
     * <p>
     * 每个元素之间，都会用一个给定的字符分隔
     * 
     * @param c
     *            分隔符
     * @param coll
     *            集合
     * @return 拼合后的字符串
     */
    public static <T> StringBuilder concat(Object c, Collection<T> coll) {
        StringBuilder sb = new StringBuilder();
        if (null == coll || coll.isEmpty())
            return sb;
        Iterator<T> it = coll.iterator();
        sb.append(it.next());
        while (it.hasNext())
            sb.append(c).append(it.next());
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
    public static <T extends Map<Object, Object>> Map<?, ?> collection2map(Class<T> mapClass,
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
     * @param col
     *            集合对象
     * @return 列表对象
     */
    @SuppressWarnings("unchecked")
    public static <E> List<E> collection2list(Collection<E> col) {
        if (null == col)
            return null;
        if (col.size() == 0)
            return new ArrayList<E>(0);
        Class<E> eleType = (Class<E>) col.iterator().next().getClass();
        return collection2list(col, eleType);
    }

    /**
     * 将集合编程变成指定类型的列表
     * 
     * @param col
     *            集合对象
     * @param eleType
     *            列表类型
     * @return 列表对象
     */
    public static <E> List<E> collection2list(Collection<?> col, Class<E> eleType) {
        if (null == col)
            return null;
        List<E> list = new ArrayList<E>(col.size());
        for (Object obj : col)
            list.add(Castors.me().castTo(obj, eleType));
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
    public static <E> E[] collection2array(Collection<E> coll) {
        if (null == coll)
            return null;
        if (coll.size() == 0)
            return (E[]) new Object[0];

        Class<E> eleType = (Class<E>) Lang.first(coll).getClass();
        return collection2array(coll, eleType);
    }

    /**
     * 将集合变成指定类型的数组
     * 
     * @param col
     *            集合对象
     * @param eleType
     *            数组元素类型
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public static <E> E[] collection2array(Collection<?> col, Class<E> eleType) {
        if (null == col)
            return null;
        Object re = Array.newInstance(eleType, col.size());
        int i = 0;
        for (Iterator<?> it = col.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (null == obj)
                Array.set(re, i++, null);
            else
                Array.set(re, i++, Castors.me().castTo(obj, eleType));
        }
        return (E[]) re;
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
    public static <T extends Map<Object, Object>> Map<?, ?> array2map(Class<T> mapClass,
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
        }
        catch (Exception e) {
            map = new HashMap<Object, Object>();
        }
        if (!mapClass.isAssignableFrom(map.getClass())) {
            throw Lang.makeThrow("Fail to create map [%s]", mapClass.getName());
        }
        return map;
    }

    /**
     * 将数组转换成一个列表。
     * 
     * @param array
     *            原始数组
     * @return 新列表
     * 
     * @see org.nutz.castor.Castors
     */
    public static <T> List<T> array2list(T[] array) {
        if (null == array)
            return null;
        List<T> re = new ArrayList<T>(array.length);
        for (T obj : array)
            re.add(obj);
        return re;
    }

    /**
     * 将数组转换成一个列表。将会采用 Castor 来深层转换数组元素
     * 
     * @param array
     *            原始数组
     * @param eleType
     *            新列表的元素类型
     * @return 新列表
     * 
     * @see org.nutz.castor.Castors
     */
    public static <T, E> List<E> array2list(Object array, Class<E> eleType) {
        if (null == array)
            return null;
        int len = Array.getLength(array);
        List<E> re = new ArrayList<E>(len);
        for (int i = 0; i < len; i++) {
            Object obj = Array.get(array, i);
            re.add(Castors.me().castTo(obj, eleType));
        }
        return re;
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
        if (null == args)
            return null;
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T map2Object(Map<?, ?> src, Class<T> toType) throws FailToCastObjectException {
        if (null == toType)
            throw new FailToCastObjectException("target type is Null");
        // 类型相同
        if (toType == Map.class)
            return (T) src;
        // 也是一种 Map
        if (Map.class.isAssignableFrom(toType)) {
            Map map;
            try {
                map = (Map) toType.newInstance();
                map.putAll(src);
                return (T) map;
            }
            catch (Exception e) {
                throw new FailToCastObjectException("target type fail to born!", unwrapThrow(e));
            }

        }
        // 数组
        if (toType.isArray())
            return (T) Lang.collection2array(src.values(), toType.getComponentType());
        // List
        if (List.class == toType) {
            return (T) Lang.collection2list(src.values());
        }

        // POJO
        Mirror<T> mirror = Mirror.me(toType);
        T obj = mirror.born();
        for (Field field : mirror.getFields()) {
            if (src.containsKey(field.getName())) {
                Object v = src.get(field.getName());
                if (null == v)
                    continue;

                Class<?> ft = field.getType();
                Object vv = null;
                // 集合
                if (v instanceof Collection) {
                    Collection c = (Collection) v;
                    // 集合到数组
                    if (ft.isArray()) {
                        vv = Lang.collection2array(c, ft.getComponentType());
                    }
                    // 集合到集合
                    else {
                        // 创建
                        Collection newCol;
                        Class eleType = Mirror.getGenericTypes(field, 0);
                        if (ft == List.class) {
                            newCol = new ArrayList(c.size());
                        } else if (ft == Set.class) {
                            newCol = new LinkedHashSet();
                        } else {
                            try {
                                newCol = (Collection) ft.newInstance();
                            }
                            catch (Exception e) {
                                throw Lang.wrapThrow(e);
                            }
                        }
                        // 赋值
                        for (Object ele : c) {
                            newCol.add(Castors.me().castTo(ele, eleType));
                        }
                        vv = newCol;
                    }
                }
                // Map
                else if (v instanceof Map && Map.class.isAssignableFrom(ft)) {
                    // 创建
                    final Map map;
                    // Map 接口
                    if (ft == Map.class) {
                        map = new HashMap();
                    }
                    // 自己特殊的 Map
                    else {
                        try {
                            map = (Map) ft.newInstance();
                        }
                        catch (Exception e) {
                            throw new FailToCastObjectException("target type fail to born!", e);
                        }
                    }
                    // 赋值
                    final Class<?> valType = Mirror.getGenericTypes(field, 1);
                    each(v, new Each<Entry>() {
                        public void invoke(int i, Entry en, int length) {
                            map.put(en.getKey(), Castors.me().castTo(en.getValue(), valType));
                        }
                    });
                    vv = map;
                }
                // 强制转换
                else {
                    vv = Castors.me().castTo(v, ft);
                }
                mirror.setValue(obj, field, vv);
            }
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
        if ((str.length() > 0 && str.charAt(0) == '{') && str.endsWith("}"))
            return (Map<String, Object>) Json.fromJson(str);
        return (Map<String, Object>) Json.fromJson("{" + str + "}");
    }

    /**
     * 根据一个格式化字符串，生成 Map 对象
     * 
     * @param fmt
     *            格式化字符串
     * @param args
     *            字符串参数
     * @return Map 对象
     */
    public static Map<String, Object> mapf(String fmt, Object... args) {
        return map(String.format(fmt, args));
    }

    /**
     * @return 一个新创建的上下文对象
     */
    public static Context context() {
        return new SimpleContext();
    }

    /**
     * 根据一个 Map 包裹成一个上下文对象
     * 
     * @param map
     *            Map 对象
     * 
     * @return 一个新创建的上下文对象
     */
    public static Context context(Map<String, Object> map) {
        return new SimpleContext(map);
    }

    /**
     * 根据一段 JSON 字符串，生产一个新的上下文对象
     * 
     * @return 一个新创建的上下文对象
     */
    public static Context context(String str) {
        return context().putAll(map(str));
    }

    /**
     * 根据一段字符串，生成一个List 对象。
     * 
     * @param str
     *            参照 JSON 标准的字符串，但是可以没有前后的中括号
     * @return List 对象
     */
    @SuppressWarnings("unchecked")
    public static List<Object> list4(String str) {
        if (null == str)
            return null;
        if ((str.length() > 0 && str.charAt(0) == '[') && str.endsWith("]"))
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
        }
        catch (Exception e) {}
        return 1;
    }

    /**
     * 如果是数组或集合取得第一个对象。 否则返回自身
     * 
     * @param obj
     *            任意对象
     * @return 第一个代表对象
     */
    public static Object first(Object obj) {
        if (null == obj)
            return obj;

        if (obj instanceof Collection<?>) {
            Iterator<?> it = ((Collection<?>) obj).iterator();
            return it.hasNext() ? it.next() : null;
        }

        if (obj.getClass().isArray())
            return Array.getLength(obj) > 0 ? Array.get(obj, 0) : null;

        return obj;
    }

    /**
     * 获取集合中的第一个元素，如果集合为空，返回 null
     * 
     * @param coll
     *            集合
     * @return 第一个元素
     */
    public static <T> T first(Collection<T> coll) {
        if (null == coll || coll.isEmpty())
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
        if (null == map || map.isEmpty())
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
     * 继续 each 循环，如果再递归，则停止递归
     */
    public static void Continue() throws ExitLoop {
        throw new ContinueLoop();
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
    public static <T> void each(Object obj, Each<T> callback) {
        each(obj, true, callback);
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
     * @param loopMap
     *            是否循环 Map，如果循环 Map 则主要看 callback 的 T，如果是 Map.Entry 则循环 Entry
     *            否循环 value。如果本值为 false， 则将 Map 当作一个完整的对象来看待
     * @param callback
     *            回调
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> void each(Object obj, boolean loopMap, Each<T> callback) {
        if (null == obj || null == callback)
            return;
        try {
            // 循环开始
            if (callback instanceof Loop)
                if (!((Loop) callback).begin())
                    return;

            // 进行循环
            Class<T> eType = Mirror.getTypeParam(callback.getClass(), 0);
            if (obj.getClass().isArray()) {
                int len = Array.getLength(obj);
                for (int i = 0; i < len; i++)
                    try {
                        callback.invoke(i, (T) Array.get(obj, i), len);
                    }
                    catch (ContinueLoop e) {}
                    catch (ExitLoop e) {
                        break;
                    }
            } else if (obj instanceof Collection) {
                int len = ((Collection) obj).size();
                int i = 0;
                for (Iterator<T> it = ((Collection) obj).iterator(); it.hasNext();)
                    try {
                        callback.invoke(i++, it.next(), len);
                    }
                    catch (ContinueLoop e) {}
                    catch (ExitLoop e) {
                        break;
                    }
            } else if (loopMap && obj instanceof Map) {
                Map map = (Map) obj;
                int len = map.size();
                int i = 0;
                if (null != eType && eType != Object.class && eType.isAssignableFrom(Entry.class)) {
                    for (Object v : map.entrySet())
                        try {
                            callback.invoke(i++, (T) v, len);
                        }
                        catch (ContinueLoop e) {}
                        catch (ExitLoop e) {
                            break;
                        }

                } else {
                    for (Object v : map.entrySet())
                        try {
                            callback.invoke(i++, (T) ((Entry) v).getValue(), len);
                        }
                        catch (ContinueLoop e) {}
                        catch (ExitLoop e) {
                            break;
                        }
                }
            } else if (obj instanceof Iterator<?>) {
                Iterator<?> it = (Iterator<?>) obj;
                int i = 0;
                while (it.hasNext()) {
                    try {
                        callback.invoke(i++, (T) it.next(), -1);
                    }
                    catch (ContinueLoop e) {}
                    catch (ExitLoop e) {
                        break;
                    }
                }
            } else
                try {
                    callback.invoke(0, (T) obj, 1);
                }
                catch (ContinueLoop e) {}
                catch (ExitLoop e) {}

            // 循环结束
            if (callback instanceof Loop)
                ((Loop) callback).end();
        }
        catch (LoopException e) {
            throw Lang.wrapThrow(e.getCause());
        }
    }

    /**
     * 安全的从一个数组获取一个元素，容忍 null 数组，以及支持负数的 index
     * <p>
     * 如果该下标越界，则返回 null
     * 
     * @param <T>
     * @param array
     *            数组，如果为 null 则直接返回 null
     * @param index
     *            下标，-1 表示倒数第一个， -2 表示倒数第二个，以此类推
     * @return 数组元素
     */
    public static <T> T get(T[] array, int index) {
        if (null == array)
            return null;
        int i = index < 0 ? array.length + index : index;
        if (i < 0 || i >= array.length)
            return null;
        return array[i];
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
        if (null == s || s.length() == 0)
            return false;
        if (s.length() > 5)
            return true;
        if ("0".equals(s))
            return false;
        s = s.toLowerCase();
        return !"false".equals(s) && !"off".equals(s) && !"no".equals(s);
    }

    /**
     * 帮你快速获得一个 DocumentBuilder，方便 XML 解析。
     * 
     * @return 一个 DocumentBuilder 对象
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder xmls() throws ParserConfigurationException {
        return Xmls.xmls();
    }

    /**
     * 对Thread.sleep(long)的简单封装,不抛出任何异常
     * 
     * @param millisecond
     *            休眠时间
     */
    public static void quiteSleep(long millisecond) {
        try {
            if (millisecond > 0)
                Thread.sleep(millisecond);
        }
        catch (Throwable e) {}
    }

    /**
     * 将字符串，变成数字对象，现支持的格式为：
     * <ul>
     * <li>null - 整数 0</li>
     * <li>23.78 - 浮点 Float</li>
     * <li>0x45 - 16进制整数 Integer</li>
     * <li>78L - 长整数 Long</li>
     * <li>69 - 普通整数 Integer</li>
     * </ul>
     * 
     * @param s
     *            参数
     * @return 数字对象
     */
    public static Number str2number(String s) {
        // null 值
        if (null == s) {
            return 0;
        }
        s = s.toUpperCase();
        // 浮点
        if (s.indexOf('.') != -1) {
            char c = s.charAt(s.length() - 1);
            if (c == 'F' || c == 'f') {
                return Float.valueOf(s);
            }
            return Double.valueOf(s);
        }
        // 16进制整数
        if (s.startsWith("0X")) {
            return Integer.valueOf(s.substring(2), 16);
        }
        // 长整数
        if (s.charAt(s.length() - 1) == 'L' || s.charAt(s.length() - 1) == 'l') {
            return Long.valueOf(s.substring(0, s.length() - 1));
        }
        // 普通整数
        Long re = Long.parseLong(s);
        if (Integer.MAX_VALUE >= re && re >= Integer.MIN_VALUE)
            return re.intValue();
        return re;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Map<String, Object>> void obj2map(Object obj,
                                                                T map,
                                                                Map<Object, Object> memo) {
        if (null == obj || memo.containsKey(obj))
            return;
        memo.put(obj, "");

        Mirror<?> mirror = Mirror.me(obj.getClass());
        Field[] flds = mirror.getFields();
        for (Field fld : flds) {
            Object v = mirror.getValue(obj, fld);
            if (null == v) {
                map.put(fld.getName(), null);
                continue;
            }
            Mirror<?> mr = Mirror.me(fld.getType());
            if (mr.isNumber()
                || mr.isBoolean()
                || mr.isChar()
                || mr.isStringLike()
                || mr.isEnum()
                || mr.isDateTimeLike()) {
                map.put(fld.getName(), v);
            } else if (memo.containsKey(v)) {
                map.put(fld.getName(), null);
            } else {
                T sub;
                try {
                    sub = (T) map.getClass().newInstance();
                }
                catch (Exception e) {
                    throw Lang.wrapThrow(e);
                }
                obj2map(v, sub, memo);
                map.put(fld.getName(), sub);
            }
        }
    }

    /**
     * 将对象转换成 Map
     * 
     * @param obj
     *            POJO 对象
     * @return Map 对象
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> obj2map(Object obj) {
        return obj2map(obj, HashMap.class);
    }

    /**
     * 将对象转换成 Map
     * 
     * @param <T>
     * @param obj
     *            POJO 对象
     * @param mapType
     *            Map 的类型
     * @return Map 对象
     */
    public static <T extends Map<String, Object>> T obj2map(Object obj, Class<T> mapType) {
        try {
            T map = mapType.newInstance();
            Lang.obj2map(obj, map, new HashMap<Object, Object>());
            return map;
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * 返回一个集合对象的枚举对象。实际上就是对 Iterator 接口的一个封装
     * 
     * @param col
     *            集合对象
     * @return 枚举对象
     */
    public static <T> Enumeration<T> enumeration(Collection<T> col) {
        final Iterator<T> it = col.iterator();
        return new Enumeration<T>() {
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public T nextElement() {
                return it.next();
            }
        };
    }

    /**
     * 将枚举对象，变成集合
     * 
     * @param enums
     *            枚举对象
     * @param cols
     *            集合对象
     * @return 集合对象
     */
    public static <T extends Collection<E>, E> T enum2collection(Enumeration<E> enums, T cols) {
        while (enums.hasMoreElements())
            cols.add(enums.nextElement());
        return cols;
    }

    /**
     * 将字符数组强制转换成字节数组。如果字符为双字节编码，则会丢失信息
     * 
     * @param cs
     *            字符数组
     * @return 字节数组
     */
    public static byte[] toBytes(char[] cs) {
        byte[] bs = new byte[cs.length];
        for (int i = 0; i < cs.length; i++)
            bs[i] = (byte) cs[i];
        return bs;
    }

    /**
     * 将整数数组强制转换成字节数组。整数的高位将会被丢失
     * 
     * @param is
     *            整数数组
     * @return 字节数组
     */
    public static byte[] toBytes(int[] is) {
        byte[] bs = new byte[is.length];
        for (int i = 0; i < is.length; i++)
            bs[i] = (byte) is[i];
        return bs;
    }

    /**
     * 判断当前系统是否为Windows
     * 
     * @return true 如果当前系统为Windows系统
     */
    public static boolean isWin() {
        try {
            String os = System.getenv("OS");
            return os != null && os.indexOf("Windows") > -1;
        }
        catch (Throwable e) {
            return false;
        }
    }

    /**
     * 使用当前线程的ClassLoader加载给定的类
     * 
     * @param className
     *            类的全称
     * @return 给定的类
     * @throws ClassNotFoundException
     *             如果无法用当前线程的ClassLoader加载
     */
    public static Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            return Class.forName(className);
        }
    }

    // 判断编译等级
    public static boolean isJDK6() {
        InputStream is = null;
        try {
            String classFileName = "/" + Lang.class.getName().replace('.', '/') + ".class";
            is = ClassTools.getClassLoader().getResourceAsStream(classFileName);
            if (is != null && is.available() > 8) {
                is.skip(7);
                return is.read() > 49;
            }
        }
        catch (Throwable e) {}
        finally {
            Streams.safeClose(is);
        }
        return false;
    }

    /**
     * 获取基本类型的默认值
     * 
     * @param pClass
     * @return 0/false,如果传入的pClass不是基本类型的类,则返回null
     */
    public static Object getPrimitiveDefaultValue(Class<?> pClass) {
        if (int.class.equals(pClass))
            return Integer.valueOf(0);
        if (long.class.equals(pClass))
            return Long.valueOf(0);
        if (short.class.equals(pClass))
            return Short.valueOf((short) 0);
        if (float.class.equals(pClass))
            return Float.valueOf(0f);
        if (double.class.equals(pClass))
            return Double.valueOf(0);
        if (byte.class.equals(pClass))
            return Byte.valueOf((byte) 0);
        if (char.class.equals(pClass))
            return Character.valueOf((char) 0);
        if (boolean.class.equals(pClass))
            return Boolean.FALSE;
        return null;
    }

    /**
     * 当一个类使用<T,K>来定义泛型时,本方法返回类的一个字段的具体类型。
     * 
     * @param me
     * @param field
     */
    public static Type getFieldType(Mirror<?> me, String field) throws NoSuchFieldException {
        return getFieldType(me, me.getField(field));
    }

    /**
     * 当一个类使用<T, K> 来定义泛型时, 本方法返回类的一个方法所有参数的具体类型
     * 
     * @param me
     * @param method
     */
    public static Type[] getMethodParamTypes(Mirror<?> me, Method method) {
        Type[] types = method.getGenericParameterTypes();
        List<Type> ts = new ArrayList<Type>();
        for (Type type : types) {
            ts.add(getGenericsType(me, type));
        }
        return ts.toArray(new Type[ts.size()]);
    }

    /**
     * 当一个类使用<T,K>来定义泛型时,本方法返回类的一个字段的具体类型。
     * 
     * @param me
     * @param field
     */
    public static Type getFieldType(Mirror<?> me, Field field) {
        Type type = field.getGenericType();
        return getGenericsType(me, type);
    }

    /**
     * 当一个类使用<T,K>来定义泛型时,本方法返回类的一个字段的具体类型。
     * 
     * @param me
     * @param type
     */
    public static Type getGenericsType(Mirror<?> me, Type type) {
        Type[] types = me.getGenericsTypes();
        if (type instanceof TypeVariable && types != null && types.length > 0) {
            Type[] tvs = me.getType().getTypeParameters();
            for (int i = 0; i < tvs.length; i++) {
                if (type.equals(tvs[i])) {
                    type = me.getGenericsType(i);
                    break;
                }
            }
        }
        return type;
    }

    /**
     * 获取一个Type类型实际对应的Class
     * 
     * @param type
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> getTypeClass(Type type) {
        Class<?> clazz = null;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            clazz = (Class<?>) pt.getRawType();
        } else if (type instanceof GenericArrayType) {
            GenericArrayType gat = (GenericArrayType) type;
            Class<?> typeClass = getTypeClass(gat.getGenericComponentType());
            return Array.newInstance(typeClass, 0).getClass();
        } else if (type instanceof TypeVariable) {
            TypeVariable tv = (TypeVariable) type;
            Type[] ts = tv.getBounds();
            if (ts != null && ts.length > 0)
                return getTypeClass(ts[0]);
        } else if (type instanceof WildcardType) {
            WildcardType wt = (WildcardType) type;
            Type[] t_low = wt.getLowerBounds();// 取其下界
            if (t_low.length > 0)
                return getTypeClass(t_low[0]);
            Type[] t_up = wt.getUpperBounds(); // 没有下界?取其上界
            return getTypeClass(t_up[0]);// 最起码有Object作为上界
        }
        return clazz;
    }

    /**
     * 返回一个type的泛型数组, 如果没有, 则直接返回null
     * 
     * @param type
     */
    public static Type[] getGenericsTypes(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return pt.getActualTypeArguments();
        }
        return null;
    }

    /**
     * 强制从字符串转换成一个 Class，将 ClassNotFoundException 包裹成 RuntimeException
     * 
     * @param <T>
     * @param name
     *            类名
     * @param type
     *            这个类型的边界
     * @return 类对象
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String name, Class<T> type) {
        Class<?> re;
        try {
            re = Class.forName(name);
            return (Class<T>) re;
        }
        catch (ClassNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
    }

    /**
     * @see #digest(String, File)
     */
    public static String md5(File f) {
        return digest("MD5", f);
    }

    /**
     * @see #digest(String, InputStream)
     */
    public static String md5(InputStream ins) {
        return digest("MD5", ins);
    }

    /**
     * @see #digest(String, CharSequence)
     */
    public static String md5(CharSequence cs) {
        return digest("MD5", cs);
    }

    /**
     * @see #digest(String, File)
     */
    public static String sha1(File f) {
        return digest("SHA1", f);
    }

    /**
     * @see #digest(String, InputStream)
     */
    public static String sha1(InputStream ins) {
        return digest("SHA1", ins);
    }

    /**
     * @see #digest(String, CharSequence)
     */
    public static String sha1(CharSequence cs) {
        return digest("SHA1", cs);
    }

    /**
     * 从数据文件计算出数字签名
     * 
     * @param algorithm
     *            算法，比如 "SHA1" 或者 "MD5" 等
     * @param f
     *            文件
     * @return 数字签名
     */
    public static String digest(String algorithm, File f) {
        return digest(algorithm, Streams.fileIn(f));
    }

    /**
     * 从流计算出数字签名，计算完毕流会被关闭
     * 
     * @param algorithm
     *            算法，比如 "SHA1" 或者 "MD5" 等
     * @param ins
     *            输入流
     * @return 数字签名
     */
    public static String digest(String algorithm, InputStream ins) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);

            byte[] bs = new byte[1024];
            int len = 0;
            while ((len = ins.read(bs)) != -1) {
                md.update(bs, 0, len);
            }

            byte[] hashBytes = md.digest();

            return fixedHexString(hashBytes);
        }
        catch (NoSuchAlgorithmException e) {
            throw Lang.wrapThrow(e);
        }
        catch (FileNotFoundException e) {
            throw Lang.wrapThrow(e);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(ins);
        }
    }

    /**
     * 从字符串计算出数字签名
     * 
     * @param algorithm
     *            算法，比如 "SHA1" 或者 "MD5" 等
     * @param cs
     *            字符串
     * @return 数字签名
     */
    public static String digest(String algorithm, CharSequence cs) {
        return digest(algorithm, Strings.getBytesUTF8(null == cs ? "" : cs),null,1);
    }

	/**
	 * 从字节数组计算出数字签名
	 * 
	 * @param algorithm
	 *            算法，比如 "SHA1" 或者 "MD5" 等
	 * @param bytes
	 *            字节数组
	 * @param salt
	 *            随机字节数组
	 * @param iterations
	 *            迭代次数
	 * @return 数字签名
	 */
	public static String digest(String algorithm, byte[] bytes, byte[] salt, int iterations) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				md.update(salt);
			}

			byte[] hashBytes = md.digest(bytes);

			for (int i = 1; i < iterations; i++) {
				md.reset();
				hashBytes = md.digest(hashBytes);
			}

			return fixedHexString(hashBytes);
        }
        catch (NoSuchAlgorithmException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public static final boolean isAndroid;
    static {
        boolean flag = false;
        try {
            Class.forName("android.Manifest");
            flag = true;
        }
        catch (Throwable e) {}
        isAndroid = flag;
    }

    /**
     * 将数组内容倒着排序
     * 
     * @param arrays
     */
    public static <T> void reverse(T[] arrays) {
        int size = arrays.length;
        for (int i = 0; i < size; i++) {
            int ih = i;
            int it = size - 1 - i;
            if (ih == it || ih > it) {
                break;
            }
            T ah = arrays[ih];
            T swap = arrays[it];
            arrays[ih] = swap;
            arrays[it] = ah;
        }
    }

    public static String simpleMetodDesc(Method method) {
        return String.format("%s.%s(...)",
                             method.getDeclaringClass().getSimpleName(),
                             method.getName());
    }
    
    public static String fixedHexString(byte[] hashBytes) {
    	StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
