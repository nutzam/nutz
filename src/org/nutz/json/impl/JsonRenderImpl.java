package org.nutz.json.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonRender;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author 有心猴(belialofking@163.com)
 */
@SuppressWarnings({"rawtypes"})
public class JsonRenderImpl implements JsonRender {

    private static String NL = "\n";

    private JsonFormat format;

    private Writer writer;

    private Set<Object> memo = new HashSet<Object>();

    public JsonFormat getFormat() {
        return format;
    }

    public void setFormat(JsonFormat format) {
        this.format = format;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void render(Object obj) throws IOException {
        if (null == obj) {
            writer.write("null");
        } else if (obj instanceof JsonRender) {
            ((JsonRender) obj).render(null);
        } else if (obj instanceof Class) {
            string2Json(((Class<?>) obj).getName());
        } else if (obj instanceof Mirror) {
            string2Json(((Mirror<?>) obj).getType().getName());
        } else {
            Mirror mr = Mirror.me(obj.getClass());
            // 枚举
            if (mr.isEnum()) {
                string2Json(((Enum) obj).name());
            }
            // 数字，布尔等
            else if (mr.isNumber()) {
                String tmp = obj.toString();
                if (tmp.equals("NaN")) {
                    // TODO 怎样才能应用上JsonFormat中是否忽略控制呢?
                    // 因为此时已经写入了key:
                    writer.write("null");
                }
                else
                    writer.write(tmp);
            }
            else if (mr.isBoolean()) {
                writer.append(obj.toString());
            }
            // 字符串
            else if (mr.isStringLike() || mr.isChar()) {
                string2Json(obj.toString());
            }
            // 日期时间
            else if (mr.isDateTimeLike()) {
                boolean flag = true;
                if (obj instanceof Date) {
                    DateFormat df = format.getDateFormat();
                    if (df != null) {
                        string2Json(df.format((Date)obj));
                        flag = false;
                    }
                }
                if (flag)
                    string2Json(format.getCastors().castToString(obj));
            }
            // 其他
            else {
                // Map
                if (obj instanceof Map) {
                    map2Json((Map) obj);
                }
                // 集合
                else if (obj instanceof Iterable) {
                    coll2Json((Iterable) obj);
                }
                // 数组
                else if (obj.getClass().isArray()) {
                    array2Json(obj);
                }
                // 普通 Java 对象
                else {
                    memo.add(obj);
                    pojo2Json(obj);
                    memo.remove(obj);
                }
            }
        }
    }

    public JsonRenderImpl() {}

    public JsonRenderImpl(Writer writer, JsonFormat format) {
        this.format = format;
        this.writer = writer;
    }

    private static boolean isCompact(JsonRenderImpl render) {
        return render.format.isCompact();
    }

    private static final Pattern p = Pattern.compile("^[a-z_A-Z$]+[a-zA-Z_0-9$]*$");

    private void appendName(String name) throws IOException {
        if (format.isQuoteName() || !p.matcher(name).find())
            string2Json(name);
        else
            writer.append(name);
    }

    private void appendPairBegin() throws IOException {
        if (!isCompact(this))
            writer.append(NL).append(Strings.dup(format.getIndentBy(),
                                                 format.getIndent()));
    }

    private void appendPairSep() throws IOException {
        writer.append(!isCompact(this) ? " :" : ":");
    }

    protected void appendPair(boolean needPairEnd, String name, Object value)
            throws IOException {
        appendPairBegin();
        appendName(name);
        appendPairSep();
        render(value);
        if (needPairEnd) {
            appendPairEnd();
        }
    }

    private boolean isIgnore(String name, Object value) {
        if (null == value && format.isIgnoreNull())
            return true;
        return format.ignore(name);
    }

    private void appendPairEnd() throws IOException {
        writer.append(',');
    }

    private void appendBraceBegin() throws IOException {
        writer.append('{');
    }

    private void appendBraceEnd() throws IOException {
        if (!isCompact(this))
            writer.append(NL).append(Strings.dup(format.getIndentBy(),
                                                 format.getIndent()));
        writer.append('}');
    }

    static class Pair {

        public Pair(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        String name;
        Object value;
    }

    @SuppressWarnings({"unchecked"})
    private void map2Json(Map map) throws IOException {
        if (null == map)
            return;
        appendBraceBegin();
        increaseFormatIndent();
        ArrayList<Pair> list = new ArrayList<Pair>(map.size());
        Set<Entry<?, ?>> entrySet = map.entrySet();
        for (Entry entry : entrySet) {
            String name = null == entry.getKey() ? "null" : entry.getKey()
                                                                 .toString();
            Object value = entry.getValue();
            if (!this.isIgnore(name, value))
                list.add(new Pair(name, value));
        }
        writeItem(list);
    }

    @SuppressWarnings("unchecked")
    private void pojo2Json(Object obj) throws IOException {
        if (null == obj)
            return;
        /*
         * Default
         */
        Class<?> type = obj.getClass();
        JsonEntity jen = Json.getEntity(Mirror.me(type));
        Method toJsonMethod = jen.getToJsonMethod();
        if (toJsonMethod != null) {
            try {
                if (toJsonMethod.getParameterTypes().length == 0) {
                    writer.append(String.valueOf(toJsonMethod.invoke(obj)));
                } else {
                    writer.append(String.valueOf(toJsonMethod.invoke(obj,
                                                                     format)));
                }
                return;
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
        }
        List<JsonEntityField> fields = jen.getFields();
        appendBraceBegin();
        increaseFormatIndent();
        ArrayList<Pair> list = new ArrayList<Pair>(fields.size());
        for (JsonEntityField jef : fields) {
            if (jef.isIgnore())
                continue;
            String name = jef.getName();
            try {
                Object value = jef.getValue(obj);

                // 判断是否应该被忽略
                if (!this.isIgnore(name, value)) {
                    Mirror mirror = null;
                    // 以前曾经输出过 ...
                    if (null != value) {
                        // zozoh: 循环引用的默认行为，应该为 null，以便和其他语言交换数据
                        mirror = Mirror.me(value);
                        if (mirror.isPojo()) {
                            if (memo.contains(value))
                                value = null;
                        }
                    }
                    // 如果是强制输出为字符串的
                    if (null != value && jef.isForceString()) {
                        // 数组
                        if (value.getClass().isArray()) {
                            String[] ss = new String[Array.getLength(value)];
                            for (int i = 0; i < ss.length; i++) {
                                ss[i] = Array.get(value, i).toString();
                            }
                            value = ss;
                        }
                        // 集合
                        else if (value instanceof Collection) {
                            Collection col = (Collection) Mirror.me(value)
                                                                .born();
                            for (Object ele : (Collection) value) {
                                col.add(ele.toString());
                            }
                            value = col;
                        }
                        // 其他统统变字符串
                        else {
                            value = value2string(jef, value);
                        }
                    } else if (jef.hasDataFormat() && null != value && value instanceof Date) {
                        value = jef.getDataFormat().format((Date)value);
                    } else if (jef.hasDataFormat() && null != value && mirror.isNumber()) {
                        value = jef.getDataFormat().format(value);
                    }

                    // 加入输出列表 ...
                    list.add(new Pair(name, value));
                }
            }
            catch (FailToGetValueException e) {}
        }
        writeItem(list);
    }

    private void writeItem(List<Pair> list) throws IOException {
        Iterator<Pair> it = list.iterator();
        while (it.hasNext()) {
            Pair p = it.next();
            appendPair(it.hasNext(), p.name, p.value);
        }
        decreaseFormatIndent();
        appendBraceEnd();
    }

    private void decreaseFormatIndent() {
        if (!isCompact(this))
            format.decreaseIndent();
    }

    private void increaseFormatIndent() {
        if (!isCompact(this))
            format.increaseIndent();
    }

    private void string2Json(String s) throws IOException {
        if (null == s)
            writer.append("null");
        else {
            char[] cs = s.toCharArray();
            writer.append(format.getSeparator());
            for (char c : cs) {
                switch (c) {
                case '"':
                    writer.append("\\\"");
                    break;
                case '\n':
                    writer.append("\\n");
                    break;
                case '\t':
                case 0x0B: // \v
                    writer.append("\\t");
                    break;
                case '\r':
                    writer.append("\\r");
                    break;
                case '\f':
                	writer.append("\\f");
                	break;
                case '\b':
                	writer.append("\\b");
                	break;
                case '\\':
                    writer.append("\\\\");
                    break;
                default:
                    if (c >= 256 && format.isAutoUnicode()) {
                        writer.append("\\u");
                        String u = Strings.fillHex(c, 4);
                        if (format.isUnicodeLower())
                        	writer.write(u.toLowerCase());
                        else
                        	writer.write(u.toUpperCase());
                    }
                    else {
                        if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                                || (c >= '\u2000' && c < '\u2100')) {
                            writer.write("\\u");
                            String hhhh = Integer.toHexString(c);
                            writer.write("0000", 0, 4 - hhhh.length());
                            writer.write(hhhh);
                        } else {
                            writer.append(c);
                        }
                    }
                }
            }
            writer.append(format.getSeparator());
        }
    }

    private void array2Json(Object obj) throws IOException {
        writer.append('[');
        int len = Array.getLength(obj) - 1;
        if (len > -1) {
            int i;
            for (i = 0; i < len; i++) {
                render(Array.get(obj, i));
                appendPairEnd();
                writer.append(' ');
            }
            render(Array.get(obj, i));
        }
        writer.append(']');
    }

    private void coll2Json(Iterable iterable) throws IOException {
        writer.append('[');
        for (Iterator<?> it = iterable.iterator(); it.hasNext();) {
            render(it.next());
            if (it.hasNext()) {
                appendPairEnd();
                writer.append(' ');
            } else
                break;
        }
        writer.append(']');
    }

    protected String value2string(JsonEntityField jef, Object value) {
        
        Format df = jef.getDataFormat();
        if (df == null) {
            Mirror mirror = Mirror.me(value);
            if (value instanceof Date) {
                df = format.getDateFormat();
            }else if (mirror.isNumber()) {
                df = format.getNumberFormat();
            }
        }
        if (df != null) {
            return df.format(value);
        }
        return value.toString();
    }
}
