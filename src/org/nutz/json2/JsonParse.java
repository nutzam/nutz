package org.nutz.json2;

import java.lang.reflect.Type;

/**
 * 解析接口
 * @author juqkai(juqkai@gmail.com)
 *
 */
public interface JsonParse {
	/**
	 * 解析
	 */
	public Object parse(Type type);
}
