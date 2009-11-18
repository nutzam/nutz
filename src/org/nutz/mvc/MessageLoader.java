package org.nutz.mvc;

import java.util.Map;

/**
 * 本地化字符串加载方式
 * <p>
 * 这个接口由注解 '@Localization' 挂接主模块上。实现类必须有一个 接受一个 String 类型参数的构造函数。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MessageLoader {

	Map<String, Map<String, String>> load();

}
