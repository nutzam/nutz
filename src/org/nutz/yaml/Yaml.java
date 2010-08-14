package org.nutz.yaml;

import java.io.Reader;
import java.io.Writer;

import org.nutz.lang.Lang;

public class Yaml {
	
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
	 * @throws YamlException
	 */
	public static Object fromYaml(Reader reader) throws YamlException {
		throw Lang.noImplement();
	}

	/**
	 * 根据指定的类型，从输入流中生成 Yaml 对象。 你的类型可以是任何 Java 对象。
	 * 
	 * @param type
	 *            对象类型
	 * @param reader
	 *            输入流
	 * @return 特定类型的 JAVA 对象
	 * @throws YamlException
	 */
	public static <T> T fromYaml(Class<T> type, Reader reader) throws YamlException {
		throw Lang.noImplement();
	}

	/**
	 * 从 Yaml 字符串中，获取 JAVA 对象。 实际上，它就是用一个 Read 包裹给定字符串。
	 * <p>
	 * 请参看函数 ‘Object fromYaml(Reader reader)’ 的描述
	 * 
	 * @param cs
	 *            Yaml 字符串
	 * @return JAVA 对象
	 * @throws YamlException
	 * 
	 * @see org.nutz.lang.Lang
	 */
	public static Object fromYaml(CharSequence cs) throws YamlException {
		throw Lang.noImplement();
	}

	/**
	 * 从 Yaml 字符串中，根据获取某种指定类型的 Yaml 对象。
	 * <p>
	 * 请参看函数 ‘<T> T fromYaml(Class<T> type, Reader reader)’ 的描述
	 * 
	 * @param type
	 *            对象类型
	 * @param cs
	 *            Yaml 字符串
	 * @return 特定类型的 JAVA 对象
	 * @throws YamlException
	 */
	public static <T> T fromYaml(Class<T> type, CharSequence cs) throws YamlException {
		throw Lang.noImplement();
	}

	/**
	 * 将一个 JAVA 对象转换成 Yaml 字符串
	 * 
	 * @param obj
	 *            JAVA 对象
	 * @return Yaml 字符串
	 */
	public static String toYaml(Object obj) {
		return toYaml(obj, null);
	}

	/**
	 * 将一个 JAVA 对象转换成 Yaml 字符串，并且可以设定 Yaml 字符串的格式化方式
	 * 
	 * @param obj
	 *            JAVA 对象
	 * @param format
	 *            Yaml 字符串格式化
	 * @return Yaml 字符串
	 */
	public static String toYaml(Object obj, YamlFormat format) {
		throw Lang.noImplement();
	}

	/**
	 * 将一个 JAVA 对象写到一个文本输出流里
	 * 
	 * @param writer
	 *            文本输出流
	 * @param obj
	 *            JAVA 对象
	 */
	public static void toYaml(Writer writer, Object obj) {
		throw Lang.noImplement();
	}

	/**
	 * 将一个 JAVA 对象写到一个文本输出流里，并且可以设定 Yaml 字符串的格式化方式
	 * 
	 * @param writer
	 *            文本输出流
	 * @param obj
	 *            JAVA 对象
	 * @param format
	 *            Yaml 字符串格式化 , 若format, 则定义为YamlFormat.nice()
	 */
	public static void toYaml(Writer writer, Object obj, YamlFormat format) {
		throw Lang.noImplement();
	}
	
}
