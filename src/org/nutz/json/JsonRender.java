package org.nutz.json;

import java.io.IOException;

/**
 * 对象-->String, 一般就是写入Writer中
 * @author wendal
 *
 */
public interface JsonRender {

    void render(Object obj) throws IOException, JsonException;
}
