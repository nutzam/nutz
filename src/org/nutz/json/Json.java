package org.nutz.json;

import static org.nutz.lang.Streams.buffr;
import static org.nutz.lang.Streams.fileInr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.json.entity.JsonEntity;
import org.nutz.json.impl.JsonRenderImpl;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.util.NutType;
import org.nutz.mapl.Mapl;

public class Json {

    // =========================================================================
    // ============================Json.fromJson================================
    // =========================================================================
    /**
     * 从一个文本输入流中，生成一个对象。
     */
    public static Object fromJson(Reader reader) throws JsonException {
    	return new org.nutz.json.impl.JsonCompileImpl().parse(reader);
        //return new org.nutz.json.impl.JsonCompileImplV2().parse(reader);
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
        Object obj = fromJson(reader);
        if (type != null)
            return Mapl.maplistToObj(obj, type);
        return obj;
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
     * 根据指定的类型，读取指定文件生成 JSON 对象。 你的类型可以是任何 Java 对象。
     * 
     * @param type
     *            对象类型，可以是范型
     * @param f
     *            文件对象
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    public static <T> T fromJsonFile(Class<T> klass, File f) {
        BufferedReader br = null;
        try {
            br = buffr(fileInr(f));
            return Json.fromJson(klass, br);
        }
        finally {
            Streams.safeClose(br);
        }
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

    // =========================================================================
    // ============================Json.toJson==================================
    // =========================================================================

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
            new JsonRenderImpl(writer, format).render(obj);
            writer.flush();
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e, JsonException.class);
        }
    }

    /**
     * 将一个 JAVA 对象写到一个文本文件里，并且可以设定 JSON 字符串的格式化方式
     * 
     * @param f
     *            文本文件
     * @param obj
     *            JAVA 对象
     */
    public static void toJsonFile(File f, Object obj) {
        toJsonFile(f, obj, null);
    }

    /**
     * 将一个 JAVA 对象写到一个文本文件里，并且可以设定 JSON 字符串的格式化方式
     * 
     * @param f
     *            文本文件
     * @param obj
     *            JAVA 对象
     * @param format
     *            JSON 字符串格式化 , 若format, 则定义为JsonFormat.nice()
     */
    public static void toJsonFile(File f, Object obj, JsonFormat format) {
        Writer writer = null;
        try {
            Files.createFileIfNoExists(f);
            writer = Streams.fileOutw(f);
            Json.toJson(writer, obj, format);
            writer.append('\n');
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        finally {
            Streams.safeClose(writer);
        }
    }

    /**
     * 清除 Json 解析器对实体解析的缓存
     */
    public static void clearEntityCache() {
        entities.clear();
    }

    /**
     * 保存所有的 Json 实体
     */
    private static final ConcurrentHashMap<String, JsonEntity> entities = new ConcurrentHashMap<String, JsonEntity>();

    /**
     * 获取一个 Json 实体
     */
    public static JsonEntity getEntity(Mirror<?> mirror) {
        JsonEntity je = entities.get(mirror.getTypeId());
        if (null == je) {
            je = new JsonEntity(mirror);
            entities.put(mirror.getTypeId(), je);
        }
        return je;
    }

    // ==================================================================================
    // ====================帮助函数======================================================

    /**
     * 从 JSON 字符串中，根据获取某种指定类型的 List 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonAsList(Class<T> eleType, CharSequence cs) {
        return (List<T>) fromJson(NutType.list(eleType), cs);
    }

    /**
     * 从 JSON 输入流中，根据获取某种指定类型的 List 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param reader
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonAsList(Class<T> eleType, Reader reader) {
        return (List<T>) fromJson(NutType.list(eleType), reader);
    }

    /**
     * 从 JSON 字符串中，根据获取某种指定类型的 数组 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] fromJsonAsArray(Class<T> eleType, CharSequence cs) {
        return (T[]) fromJson(NutType.array(eleType), cs);
    }

    /**
     * 从 JSON 输入流中，根据获取某种指定类型的 数组 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param reader
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] fromJsonAsArray(Class<T> eleType, Reader reader) {
        return (T[]) fromJson(NutType.array(eleType), reader);
    }

    /**
     * 从 JSON 字符串中，根据获取某种指定类型的 Map 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param cs
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> fromJsonAsMap(Class<T> eleType, CharSequence cs) {
        return (Map<String, T>) fromJson(NutType.mapStr(eleType), cs);
    }

    /**
     * 从 JSON 输入流中，根据获取某种指定类型的 Map 对象。
     * <p>
     * 请参看函数 ‘<T> T fromJson(Class<T> type, Reader reader)’ 的描述
     * 
     * @param eleType
     *            对象类型
     * @param reader
     *            JSON 字符串
     * @return 特定类型的 JAVA 对象
     * @throws JsonException
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> fromJsonAsMap(Class<T> eleType, Reader reader) {
        return (Map<String, T>) fromJson(NutType.mapStr(eleType), reader);
    }

    // ==============================================================================
}
