package org.nutz.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.nutz.lang.Lang;

/**
 * JSON 转换工具
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class Json {

	/**
	 * 从一个文本输入流中，生成一个对象。根据内容不同，可能会生成
	 * <ul>
	 * <li>Map
	 * <li>List
	 * <li>Integer 或者 Float
	 * <li>String
	 * </ul>
	 * 
	 * @param reader
	 *            输入流
	 * @return JAVA 对象
	 * @throws JsonException
	 */
	public static Object fromJson(Reader reader) throws JsonException {
		return new JsonParsing(reader).parseFromJson(null);
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
	public static <T> T fromJson(Class<T> type, Reader reader) throws JsonException {
		return new JsonParsing(reader).parseFromJson(type);
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
			if(format == null)
				format = JsonFormat.nice();
			new JsonRendering(writer, format).render(obj);
			writer.flush();
		} catch (IOException e) {
			throw Lang.wrapThrow(e, JsonException.class);
		}
	}

}
