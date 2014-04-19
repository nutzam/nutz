package org.nutz.json;

import java.io.IOException;
import java.io.Writer;

/**
 * 对象-->String, 一般就是写入Writer中
 * @author wendal
 *
 */
public interface JsonRender {

    void render(Object obj) throws IOException, JsonException;

    void setWriter(Writer writer);

    void setFormat(JsonFormat jsonFormat);
}
