package org.nutz.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.nutz.json.entity.JsonEntity;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Objs;
import org.nutz.lang.stream.StringReader;
import org.nutz.lang.util.NutType;

public class Json {

	/**
	 * 保存所有的 Json 实体
	 */
	private static final Map<String, JsonEntity> entities = new WeakHashMap<String, JsonEntity>();

	/**
	 * 获取一个 Json 实体
	 * 
	 * @param classOfT
	 *            类型
	 * @return 实体对象
	 */
	static JsonEntity getEntity(Class<?> classOfT) {
		JsonEntity je = entities.get(classOfT.getName());
		if (null == je)
			synchronized (entities) {
				je = entities.get(classOfT.getName());
				if (null == je) {
					je = new JsonEntity(Mirror.me(classOfT));
					entities.put(classOfT.getName(), je);
				}
			}
		return je;
	}

	/**
	 * 从一个文本输入流中，生成一个对象。根据内容不同，可能会生成
	 * <ul>
	 * <li>Map
	 * <li>List
	 * <li>Integer 或者 Float
	 * <li>String
	 * <li>Boolean
	 * <li>Char
	 * <li>Long Double
	 * </ul>
	 * 
	 * @param reader
	 *            输入流
	 * @return JAVA 对象
	 * @throws JsonException
	 */
	public static Object fromJson(Reader reader) throws JsonException {
		return new JsonCompile().parse(reader);
	}

	/**
	 * 根据指定的类型，从输入流中生成 JSON 对象。 你的类型可以是任何 Java 对象。
	 * 
	 * @param type
	 *            对象类型
	 * @param reader
	 *            输入流
	 * @return 特定类型的 JAVA 对象
	 * @throws JsonException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromJson(Class<T> type, Reader reader) throws JsonException {
		return (T) parse(type, reader);
	}

	/**
	 * 根据指定的类型，从输入流中生成 JSON 对象。 你的类型可以是任何 Java 对象。
	 * 
	 * @param type
	 *            对象类型，可以是范型
	 * @param reader
	 *            文本输入流
	 * @return 特定类型的 JAVA 对象
	 * @throws JsonException
	 */
	public static Object fromJson(Type type, Reader reader) throws JsonException {
		return parse(type, reader);
	}
	
	private static Object parse(Type type, Reader reader) {
        return Objs.convert(new JsonCompile().parse(reader), type);
    }

	/**
	 * 根据指定的类型，从输入流中生成 JSON 对象。 你的类型可以是任何 Java 对象。
	 * 
	 * @param type
	 *            对象类型，可以是范型
	 * @param cs
	 *            JSON 字符串
	 * @return 特定类型的 JAVA 对象
	 * @throws JsonException
	 */
	public static Object fromJson(Type type, CharSequence cs) throws JsonException {
		return fromJson(type, Lang.inr(cs));
	}

	/**
	 * 从 JSON 字符串中，获取 JAVA 对象。 实际上，它就是用一个 Read 包裹给定字符串。
	 * <p>
	 * 请参看函数 ‘Object fromJson(Reader reader)’ 的描述
	 * 
	 * @param cs
	 *            JSON 字符串
	 * @return JAVA 对象
	 * @throws JsonException
	 * 
	 * @see org.nutz.lang.Lang
	 */
	public static Object fromJson(CharSequence cs) throws JsonException {
		return fromJson(Lang.inr(cs));
	}

	/**
	 * 从 JSON 字符串中，根据获取某种指定类型的 JSON 对象。
	 * <p>
	 * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
	 * 
	 * @param type
	 *            对象类型
	 * @param cs
	 *            JSON 字符串
	 * @return 特定类型的 JAVA 对象
	 * @throws JsonException
	 */
	public static <T> T fromJson(Class<T> type, CharSequence cs) throws JsonException {
		return fromJson(type, Lang.inr(cs));
	}
	
	/**
     * 从 JSON 字符串中，根据获取某种指定类型的 List 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonAsList(Class<T> eleType, CharSequence cs){
	    return (List<T>) fromJson(NutType.list(eleType), cs);
	}
	/**
     * 从 JSON 输入流中，根据获取某种指定类型的 List 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonAsList(Class<T> eleType, Reader reader){
	    return (List<T>) fromJson(NutType.list(eleType), reader);
	}
	
	/**
     * 从 JSON 字符串中，根据获取某种指定类型的 数组 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> T[] fromJsonAsArray(Class<T> eleType, CharSequence cs){
	    return (T[]) fromJson(NutType.array(eleType), cs);
	}
	/**
     * 从 JSON 输入流中，根据获取某种指定类型的 数组 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> T[] fromJsonAsArray(Class<T> eleType, Reader reader){
	    return (T[]) fromJson(NutType.array(eleType), reader);
	}
	/**
     * 从 JSON 字符串中，根据获取某种指定类型的 Map 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> Map<String,T> fromJsonAsMap(Class<T> eleType, CharSequence cs){
	    return (Map<String, T>) fromJson(NutType.mapStr(eleType), cs);
	}
	/**
     * 从 JSON 输入流中，根据获取某种指定类型的 Map 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param type
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
	@SuppressWarnings("unchecked")
    public static <T> Map<String,T> fromJsonAsMap(Class<T> eleType, Reader reader){
	    return (Map<String, T>) fromJson(NutType.mapStr(eleType), reader);
	}

	/**
	 * 将一个 JAVA 对象转换成 JSON 字符串
	 * 
	 * @param obj
	 *            JAVA 对象
	 * @return JSON 字符串
	 */
	public static String toJson(Object obj) {
		return toJson(obj, null);
	}

	/**
	 * 将一个 JAVA 对象转换成 JSON 字符串，并且可以设定 JSON 字符串的格式化方式
	 * 
	 * @param obj
	 *            JAVA 对象
	 * @param format
	 *            JSON 字符串格式化
	 * @return JSON 字符串
	 */
	public static String toJson(Object obj, JsonFormat format) {
		StringBuilder sb = new StringBuilder();
		toJson(Lang.opw(sb), obj, format);
		return sb.toString();
	}

	/**
	 * 将一个 JAVA 对象写到一个文本输出流里
	 * 
	 * @param writer
	 *            文本输出流
	 * @param obj
	 *            JAVA 对象
	 */
	public static void toJson(Writer writer, Object obj) {
		toJson(writer, obj, null);
	}

	/**
	 * 将一个 JAVA 对象写到一个文本输出流里，并且可以设定 JSON 字符串的格式化方式
	 * 
	 * @param writer
	 *            文本输出流
	 * @param obj
	 *            JAVA 对象
	 * @param format
	 *            JSON 字符串格式化 , 若format, 则定义为JsonFormat.nice()
	 */
	public static void toJson(Writer writer, Object obj, JsonFormat format) {
		try {
			if (format == null)
				format = JsonFormat.nice();
			new JsonRendering(writer, format).render(obj);
			writer.flush();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e, JsonException.class);
		}
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Object fromJsonMerge(String... srcs) {
        List list = new ArrayList();
        for(String src : srcs){
            Reader reader = new StringReader(src);
            Object obj = new JsonCompile().parse(reader);
            list.add(obj);
        }
        return Objs.merge(list.toArray());
    }

}
