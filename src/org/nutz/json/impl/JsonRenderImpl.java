package org.nutz.json.impl;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.Format;
import java.util.ArrayList;
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
import org.nutz.json.JsonTypeHandler;
import org.nutz.json.entity.JsonEntityField;
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

    private boolean compact;

    /**
     * 缩进
     */
    private int indent;

    public JsonFormat getFormat() {
        return format;
    }

    @Override
    public void setFormat(JsonFormat format) {
        this.format = format;
        this.compact = format.isCompact();
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void render(Object obj) throws IOException {
        if (null == obj) {
            appendNull();
            return;
        }
        Mirror mirror = Mirror.me(obj);
        for (JsonTypeHandler handler : Json.getTypeHandlers()) {
            if (handler.supportToJson(mirror, obj, format)) {
                if (handler.shallCheckMemo()) {
                    if (memo.contains(obj)) {
                        writer.write("null");
                        return;
                    }
                    memo.add(obj);
                    handler.toJson(mirror, obj, this, format);
                    memo.remove(obj);
                }
                else
                    handler.toJson(mirror, obj, this, format);
                return;
            }
        }
        // 理论上不会到这来,防御用
        this.string2Json(String.valueOf(obj));
    }

    public JsonRenderImpl() {}

    public JsonRenderImpl(Writer writer, JsonFormat format) {
        this.writer = writer;
        setFormat(format);
    }

    private static final Pattern p = Pattern.compile("^[a-z_A-Z$]+[a-zA-Z_0-9$]*$");

    @Override
    public void appendName(String name) throws IOException {
        if (format.isQuoteName() || !p.matcher(name).find())
            string2Json(name);
        else
            writer.append(name);
    }

    @Override
    public void appendPairBegin() throws IOException {
        if (!compact) {
            writer.append(NL);
            doIntent();
        }
    }

    @Override
    public void appendPairSep() throws IOException {
        writer.append(!compact ? ": " : ":");
    }

    @Override
    public void appendPair(boolean needPairEnd, String name, Object value) throws IOException {
        appendPairBegin();
        appendName(name);
        appendPairSep();
        render(value);
        if (needPairEnd) {
            appendPairEnd();
        }
    }

    @Override
    public boolean isIgnore(String name, Object value) {
        if (null == value && format.isIgnoreNull())
            return true;
        return format.ignore(name);
    }

    @Override
    public void appendPairEnd() throws IOException {
        writer.append(',');
    }

    @Override
    public void appendBraceBegin() throws IOException {
        writer.append('{');
    }

    @Override
    public void appendBraceEnd() throws IOException {
        if (!compact) {
            writer.append(NL);
            doIntent();
        }
        writer.append('}');
    }

    @SuppressWarnings({"unchecked"})
    public void map2Json(Map map) throws IOException {
        if (null == map)
            return;
        appendBraceBegin();
        increaseFormatIndent();
        ArrayList<JsonPair> list = new ArrayList<JsonPair>(map.size());
        Set<Entry<?, ?>> entrySet = map.entrySet();
        for (Entry entry : entrySet) {
            String name = null == entry.getKey() ? "null" : entry.getKey().toString();
            Object value = entry.getValue();
            if (!this.isIgnore(name, value))
                list.add(new JsonPair(name, value));
        }
        writeItem(list);
    }

    @Override
    public void writeItem(List<JsonPair> list) throws IOException {
        Iterator<JsonPair> it = list.iterator();
        while (it.hasNext()) {
            JsonPair p = it.next();
            appendPair(it.hasNext(), p.name, p.value);
        }
        decreaseFormatIndent();
        appendBraceEnd();
    }

    @Override
    public void decreaseFormatIndent() {
        if (!compact)
            indent--;
    }

    @Override
    public void increaseFormatIndent() {
        if (!compact)
            indent++;
    }

    public void string2Json(String s) throws IOException {
        if (null == s)
            appendNull();
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
                    } else {
                        if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
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

    @Override
    public String value2string(JsonEntityField jef, Object value) {
        Format df = jef.getDataFormat();
        if (df == null) {
            Mirror mirror = Mirror.me(value);
            if (value instanceof Date) {
                df = format.getDateFormat();
            } else if (mirror.isNumber()) {
                df = format.getNumberFormat();
            }
        }
        if (df != null) {
            if (df instanceof DateFormat)
                return doDateFormat((Date) value, (DateFormat) df);
            return df.format(value);
        }
        return value.toString();
    }

    protected void doIntent() throws IOException {
        for (int i = 0; i < indent; i++)
            writer.write(format.getIndentBy());
    }

    protected void appendNull() throws IOException {
        if (format.isNullAsEmtry())
            writer.write("\"\"");
        else
            writer.write("null");
    }

    protected String doDateFormat(Date date, DateFormat df) {
        if (df == null)
            df = format.getDateFormat();
        if (df != null) {
            if (format.getTimeZone() != null)
                df.setTimeZone(format.getTimeZone());
            return df.format(date);
        }
        return null;
    }

    @Override
    public void writeRaw(String raw) throws IOException {
        writer.write(raw);
    }

    @Override
    public boolean memoContains(Object obj) {
        return memo.contains(obj);
    }

}
