package org.nutz.json;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.nutz.json.entity.JsonEntityField;
import org.nutz.json.impl.JsonPair;

/**
 * 对象-->String, 一般就是写入Writer中
 * @author wendal
 *
 */
public interface JsonRender {

    void render(Object obj) throws IOException, JsonException;

    void setWriter(Writer writer);
    
    Writer getWriter();
    
    void setFormat(JsonFormat jsonFormat);
    
    /**
     * 循环依赖的检查
     */
    boolean memoContains(Object obj);
    
    void string2Json(String s) throws IOException;
    
    @SuppressWarnings("rawtypes")
    void map2Json(Map map) throws IOException;

    void writeRaw(String raw) throws IOException;

    void appendBraceEnd() throws IOException;

    void appendBraceBegin() throws IOException;

    void appendPairEnd() throws IOException;

    boolean isIgnore(String name, Object value);

    void appendPair(boolean needPairEnd, String name, Object value) throws IOException;

    void appendPairSep() throws IOException;

    void appendPairBegin() throws IOException;

    void appendName(String name) throws IOException;

    void increaseFormatIndent();

    void decreaseFormatIndent();

    String value2string(JsonEntityField jef, Object value);

    void writeItem(List<JsonPair> list) throws IOException;
}
