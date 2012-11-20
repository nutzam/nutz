package org.nutz.mapl.impl.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.nutz.castor.Castors;
import org.nutz.el.El;
import org.nutz.json.Json;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.Context;
import org.nutz.mapl.Mapl;
import org.nutz.mapl.MaplConvert;

/**
 * 对象转换 将MapList结构转换成对应的对象 TODO 具有循环引用的对象应该会出问题
 * 
 * @author juqkai(juqkai@gmail.com)
 */
public class ObjConvertImpl implements MaplConvert {

    // 路径
    Stack<String> path = new Stack<String>();
    // 对象缓存
    Context context = Lang.context();

    private Type type;

    public ObjConvertImpl(Type type) {
        this.type = type;
    }

    /**
     * 这个实现, 主要将 List, Map 的对象结构转换成真实的对象.
     * <p>
     * 规则:
     * <ul>
     * <li>对象以Map存储, key为属性名, value为属性值
     * <li>数组以List存储
     * <li>Map直接存储为Map
     * <li>List直接存储为List
     * <li>只要不是List, Map 存储的, 都认为是可以直接写入对象的. TODO 这点可以调整一下.
     * </ul>
     */
    public Object convert(Object model) {
        if (model == null)
            return null;
        if (type == null)
            return model;
        // obj是基本数据类型或String
        if (!(model instanceof Map) && !(model instanceof List)) {
            return Castors.me().castTo(model, Lang.getTypeClass(type));
        }

        return inject(model, type);
    }

    Object inject(Object model, Type type) {
        if (model == null) {
            return null;
        }
        Mirror<?> me = Mirror.me(type);
        Object obj = null;
        if (Collection.class.isAssignableFrom(me.getType())) {
            obj = injectCollection(model, me);
        } else if (Map.class.isAssignableFrom(me.getType())) {
            obj = injectMap(model, me);
        } else if (me.getType().isArray()) {
            obj = injectArray(model, me);
        } else {
            obj = injectObj(model, me);
        }
        if (path.size() > 0)
            path.pop();
        return obj;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object injectArray(Object model, Mirror<?> me) {
        Class<?> clazz = me.getType().getComponentType();
        List list = (List) model;
        List vals = new ArrayList();
        int j = 0;
        for (Object obj : list) {
            if (isLeaf(obj)) {
                vals.add(Castors.me().castTo(obj, clazz));
                continue;
            }
            path.push("a" + (j++));
            vals.add(inject(obj, clazz));
        }
        Object obj = Array.newInstance(clazz, vals.size());
        for (int i = 0; i < vals.size(); i++) {
            Array.set(obj, i, vals.get(i));
        }
        return obj;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object injectMap(Object model, Mirror<?> me) {
        Map re = null;
        if (me.isInterface()) {
            re = new HashMap();
        } else {
            re = (Map) me.born();
        }

        Map map = (Map) model;
        if (me.getGenericsTypes() == null) {
            re.putAll(map);
            return re;
        }

        Type type = me.getGenericsType(1);
        for (Object key : map.keySet()) {
            Object val = map.get(key);
            // 转换Key
            if (!isLeaf(key)) {
                key = inject(key, me.getGenericsType(0));
            }
            // 转换val并填充
            if (isLeaf(val)) {
                re.put(key, Castors.me().castTo(val, Lang.getTypeClass(type)));
                continue;
            }
            path.push(key.toString());
            re.put(key, inject(val, type));
        }
        return re;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object injectCollection(Object model, Mirror<?> me) {
        if (! (model instanceof Collection)) {
            throw Lang.makeThrow("Not a Collection --> " + model.getClass());
        }
        Collection re = null;
        if (!me.isInterface()) {
            re = (Collection) me.born();
        } else {
            re = makeCollection(me);
        }
        if (me.getGenericsTypes() == null) {
            re.addAll((Collection) model);
            return re;
        }
        Type type = me.getGenericsType(0);
        int j = 0;
        for (Object obj : (Collection) model) {
            if (isLeaf(obj)) {
                re.add(Castors.me().castTo(obj, Lang.getTypeClass(type)));
                continue;
            }
            path.push("a" + (j++));
            re.add(inject(obj, type));
        }
        return re;
    }

    @SuppressWarnings("rawtypes")
    private Collection makeCollection(Mirror<?> me) {
        if (List.class.isAssignableFrom(me.getType())) {
            return new ArrayList();
        }
        if (Set.class.isAssignableFrom(me.getType())) {
            return new HashSet();
        }
        throw new RuntimeException("不支持的类型!");
    }

    @SuppressWarnings("unchecked")
    private Object injectObj(Object model, Mirror<?> mirror) {
        // zzh: 如果是 Object，那么就不要转换了
        if (mirror.getType() == Object.class)
            return model;
        Object obj = mirror.born();
        context.set(fetchPath(), obj);
        Map<String, ?> map = (Map<String, ?>) model;

        JsonEntity jen = Json.getEntity(mirror);
        for (String key : map.keySet()) {
            JsonEntityField jef = jen.getField(key);
            if (jef == null) {
                continue;
            }

            Object val = map.get(jef.getName());
            if (val == null) {
                continue;
            }

            if (isLeaf(val)) {
                if (val instanceof El) {
                    val = ((El) val).eval(context);
                }
                // zzh@2012-09-14: 暂时去掉 createBy 吧
                // jef.setValue(obj, Castors.me().castTo(jef.createValue(obj,
                // val, null), Lang.getTypeClass(jef.getGenericType())));
                // jef.setValue(obj, jef.createValue(obj, val, null));
                jef.setValue(obj, Mapl.maplistToObj(val, jef.getGenericType()));
                continue;
            } else {
                path.push(key);
                // jef.setValue(obj, Mapl.maplistToObj(val,
                // me.getGenericsType(0)));
                jef.setValue(obj, Mapl.maplistToObj(val, jef.getGenericType()));
                // zzh@2012-09-14: 暂时去掉 createBy 吧
                // jef.setValue(obj, jef.createValue(obj, val,
                // me.getGenericsType(0)));
            }
        }
        return obj;
    }

    private boolean isLeaf(Object obj) {
        if (obj instanceof Map) {
            return false;
        }
        if (obj instanceof List) {
            return false;
        }
        return true;
    }

    private String fetchPath() {
        StringBuffer sb = new StringBuffer();
        sb.append("root");
        for (String item : path) {
            if (item.charAt(0) != 'a') {
                sb.append("m");
            }
            sb.append(item);
        }
        return sb.toString();
    }
}
