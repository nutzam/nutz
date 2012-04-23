package org.nutz.json.filter;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.nutz.json.JsonCompile;
import org.nutz.json.JsonFilter;

/**
 * 扩充 JsonCompile 实现, 以使它支持对JSON字符串的过滤处理.
 * <p>
 * 规则:
 * <ul> 
 * <li>1. 要定义过滤或是包含, 都直接以对象关联的方式写出, 如: user.name, 
 * <li>2. 不区分 Map, List 全部都使用 1 中的形式. 基本这里指的 Map, List 是指 JsonCompile 转换的中间对象, 而非 JAVA 属性中的 Map, List. 注意概念
 * <li>3. 包含还是排除, 以 type 属性做标识, true 为包含, false 为排除.
 * <li>4. 同一时间只支持一种关系.
 * </ul>
 * @author juqkai(juqkai@gmail.com)
 */
public class JsonCompileFilter extends JsonCompile{
    private JsonFilter filter;
    public Object parse(Reader reader, List<String> mates, boolean type) {
        filter = new JsonFilter(mates, type);
        return super.parse(reader);
    }
    /**
     * 在Map解释添加过滤
     */
    protected void parseMapItem(Map<String, Object> map) throws IOException {
        String key = fetchKey();
        filter.pushPath(key);
        Object val = parseFromHere();
        if(filter.include()){
            map.put(key, val);
        }
        filter.pollPath();
    }
}
