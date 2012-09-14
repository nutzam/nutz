package org.nutz.mapl.impl.compile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Mirror;
import org.nutz.mapl.MaplCompile;

/**
 * 将对象理解成Map+List
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class ObjCompileImpl implements MaplCompile<Object> {

    private Map<Object, Object> memo = new HashMap<Object, Object>();

    @SuppressWarnings("rawtypes")
    public Object parse(Object obj) {
        if (null == obj) {
            return null;
        } else if (obj instanceof ObjCompileImpl) {
            return ((ObjCompileImpl) obj).parse(null);
        } else if (obj instanceof Class) {
            return obj;
        } else if (obj instanceof Mirror) {
            return ((Mirror<?>) obj).getType().getName();
        } else {
            Mirror mr = Mirror.me(obj.getClass());
            // 枚举
            if (mr.isEnum()) {
                return obj;
            }
            // 数字，布尔等
            else if (mr.isNumber() || mr.isBoolean()) {
                return obj;
            }
            // 字符串
            else if (mr.isStringLike() || mr.isChar()) {
                return obj;
            }
            // 日期时间
            else if (mr.isDateTimeLike()) {
                return obj;
            }
            // 其他
            else {
                // 既然到了这里, 那么断定它只有List, Array, Map, Object这4种类型
                // 是否已经存在(循环引用)
                if (memo.containsKey(obj)) {
                    return memo.get(obj);
                } else {
                    // 这里使用了一个小小的占坑技巧,
                    if (obj instanceof Collection || obj.getClass().isArray()) {
                        List<Object> list = new ArrayList<Object>();
                        memo.put(obj, list);
                        // 集合
                        if (obj instanceof Collection) {
                            return coll2Json((Collection) obj, list);
                        }
                        // 数组
                        return array2Json(obj, list);
                    } else {
                        Map<String, Object> map = new HashMap<String, Object>();
                        memo.put(obj, map);
                        // Map
                        if (obj instanceof Map) {
                            return map2Json((Map) obj, map);
                        }
                        // 普通 Java 对象
                        return pojo2Json(obj, map);
                    }
                }
            }
        }
    }

    static class Pair {
        public Pair(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        String name;
        Object value;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Map<String, Object> map2Json(Map map, Map<String, Object> valMap) {
        if (null == map)
            return null;
        ArrayList<Pair> list = new ArrayList<Pair>(map.size());
        Set<Entry<?, ?>> entrySet = map.entrySet();
        for (Entry entry : entrySet) {
            String name = null == entry.getKey() ? "null" : entry.getKey().toString();
            Object value = entry.getValue();
            list.add(new Pair(name, value));
        }
        return writeItem(list, valMap);
    }

    private Map<String, Object> pojo2Json(Object obj, Map<String, Object> map) {
        if (null == obj)
            return null;
        Class<? extends Object> type = obj.getClass();
        JsonEntity jen = Json.getEntity(Mirror.me(type));
        List<JsonEntityField> fields = jen.getFields();
        ArrayList<Pair> list = new ArrayList<Pair>(fields.size());
        for (JsonEntityField jef : fields) {
            String name = jef.getName();
            try {
                Object value = jef.getValue(obj);
                if (null != value) {
                    // 递归
                    Mirror<?> mirror = Mirror.me(value);
                    if (mirror.isPojo()) {
                        value = parse(value);
                    }
                }
                // 加入输出列表 ...
                list.add(new Pair(name, value));
            }
            catch (FailToGetValueException e) {}
        }
        return writeItem(list, map);
    }

    private Map<String, Object> writeItem(List<Pair> list, Map<String, Object> map) {
        for (Pair p : list) {
            map.put(p.name, p.value);
        }
        return map;
    }

    private List<Object> array2Json(Object obj, List<Object> list) {
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            list.add(parse(Array.get(obj, i)));
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    private List<Object> coll2Json(Collection iterable, List<Object> list) {
        for (Iterator<?> it = iterable.iterator(); it.hasNext();) {
            list.add(parse(it.next()));
        }
        return list;
    }

}
