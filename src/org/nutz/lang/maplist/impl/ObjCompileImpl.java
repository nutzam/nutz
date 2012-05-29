package org.nutz.lang.maplist.impl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.FailToGetValueException;
import org.nutz.lang.Mirror;
import org.nutz.lang.maplist.MapListCompile;

/**
 * 将对象理解成Map+List
 * @author juqkai(juqkai@gmail.com)
 */
public class ObjCompileImpl implements MapListCompile<Object>{
    private JsonFormat format;
    
    private Set<Object> memo = new HashSet<Object>();
    
    
    @SuppressWarnings("rawtypes")
    public Object parse(Object obj) {
        if (null == obj) {
            return null;
        } else if (obj instanceof ObjCompileImpl) {
            return ((ObjCompileImpl)obj).parse(null);
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
                // Map
                if (obj instanceof Map) {
                    return map2Json((Map) obj);
                }
                // 集合
                else if (obj instanceof Collection) {
                    return coll2Json((Collection) obj);
                }
                // 数组
                else if (obj.getClass().isArray()) {
                    return array2Json(obj);
                }
                // 普通 Java 对象
                else {
                    return pojo2Json(obj);
                }
            }
        }
    }
    
    public ObjCompileImpl(JsonFormat format) {
        this.format = format;
    }

    private static boolean isCompact(ObjCompileImpl render) {
        return render.format.isCompact();
    }

    private boolean isIgnore(String name, Object value) {
        if (null == value && format.isIgnoreNull())
            return true;
        return format.ignore(name);
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
    private Map<String, Object> map2Json(Map map) {
        if (null == map)
            return null;
        increaseFormatIndent();
        ArrayList<Pair> list = new ArrayList<Pair>(map.size());
        Set<Entry<?, ?>> entrySet = map.entrySet();
        for (Entry entry : entrySet) {
            String name = null == entry.getKey() ? "null" : entry.getKey().toString();
            Object value = entry.getValue();
            if (!this.isIgnore(name, value))
                list.add(new Pair(name, value));
        }
        return writeItem(list);
    }

    private Map<String, Object> pojo2Json(Object obj) {
        if (null == obj)
            return null;
        Class<? extends Object> type = obj.getClass();
        /*
         * Default
         */
        JsonEntity jen = Json.getEntity(type);
        List<JsonEntityField> fields = jen.getFields();
        increaseFormatIndent();
        ArrayList<Pair> list = new ArrayList<Pair>(fields.size());
        for (JsonEntityField jef : fields) {
            String name = jef.getName();
            try {
                Object value = jef.getValue(obj);
                // 判断是否应该被忽略
                if (!this.isIgnore(name, value)) {
                    // 以前曾经输出过 ...
                    if (null != value) {
                        // zozoh: 循环引用的默认行为，应该为 null，以便和其他语言交换数据
                        Mirror<?> mirror = Mirror.me(value);
                        if (mirror.isPojo()) {
                            if (memo.contains(value))
                                value = null;
                            else
                                memo.add(value);
                        }
                    }
                    // 加入输出列表 ...
                    list.add(new Pair(name, value));
                }
            }
            catch (FailToGetValueException e) {}
        }
        return writeItem(list);
    }
    
    private Map<String, Object> writeItem(List<Pair> list){
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<Pair> it = list.iterator();
        while (it.hasNext()) {
            Pair p = it.next();
            map.put(p.name, p.value);
        }
        return map;
    }

    private void increaseFormatIndent() {
        if (!isCompact(this))
            format.increaseIndent();
    }

    private List<Object> array2Json(Object obj) {
        List<Object> list = new ArrayList<Object>();
        int len = Array.getLength(obj);
        for (int i = 0; i < len; i++) {
            list.add(parse(Array.get(obj, i)));
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    private List<Object> coll2Json(Collection iterable) {
        List<Object> list = new ArrayList<Object>();
        for (Iterator<?> it = iterable.iterator(); it.hasNext();) {
            list.add(parse(it.next()));
        }
        return list;
    }

}
