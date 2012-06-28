package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 路径映射,以JSON格式进行解析
 * 例:
 *     "{
 *         'key1':'url1'
 *         ,'key2':'url2'
 *         ,'key3':'url3'
 *         ,'key4':'url4'
 *  }"
 * 需要注意的是,如果使用这个功能的话,在使用@Ok或@Fail时必须指定value, key.
 * 基本value代表url前缀,即:jsp:,->:,>>:等这些内容
 * @author juqkai(juqkai@gmail.com)
 */
@Target(ElementType.TYPE)
@Documented
public @interface PathMap {
    String value();
}
