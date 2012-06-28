package org.nutz.mvc;

import java.util.Map;

/**
 * 本地化字符串加载方式
 * <p>
 * 这个接口由注解 '@Localization' 挂接主模块上。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface MessageLoader {

    /**
     * 本函数将根据传入的 "refer" 参数，返回一个 Map <br>
     * Map 的键是语言的名称，比如 "en_US", "zh_CN" 等，<br>
     * 你会通过 Mvcs.setLocalizationKey 来直接使用这个键值
     * <p>
     * 与键对应的是一个消息字符串的 Map, 该 Map 的键就是消息键，值就是消息内容
     * 
     * @param refer
     *            参考值。来自 '@Localization.value'
     * @return 多国语言字符串的 Map
     */
    Map<String, Map<String, Object>> load(String refer);

}
