package org.nutz.json;

import java.io.Reader;

/**
 * 将Json字符串,转换为一个标准对象(Map/List/基本数据类型)
 */
public interface JsonParser {

    Object parse(Reader reader) throws Exception;
    
}
