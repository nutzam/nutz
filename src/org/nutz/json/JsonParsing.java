package org.nutz.json;

import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.castor.Castors;
import org.nutz.json.entity.JsonEntity;
import org.nutz.json.entity.JsonEntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class JsonParsing {

	public static Object parse(Type type, Reader reader) {
		Object obj = new JsonCompile().parse(reader);
		return convert(type, obj);
	}

	@SuppressWarnings("unchecked")
	public static Object convert(Type type, Object obj) {
		if (obj == null)
			return null;
		if (type == null)
			return obj;
		if (obj instanceof Map) {
			return map2Object(type, (Map<String, Object>) obj);
		} else if (obj instanceof List) {
			return list2Object(type, (List<Object>) obj);
		} else {// obj是基本数据类型或String
			return Castors.me().castTo(obj, Lang.getTypeClass(type));
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static Object map2Object(Type type, Map<String, Object> map) {
		Class<?> clazz = Lang.getTypeClass(type);
		Mirror<?> me = Mirror.me(clazz);
		if (Map.class.isAssignableFrom(clazz)) {
			Map re = null;
			if (clazz.isInterface())
				re = new HashMap();
			else
				re = (Map) me.born();
			if (type instanceof ParameterizedType) {
				// 看来有泛型信息哦
				ParameterizedType pt = (ParameterizedType) type;
				Type[] ts = pt.getActualTypeArguments();
				Type tt = null;
				if (ts != null && ts.length > 1)
					tt = Lang.getTypeClass(ts[1]);// TODO 多层的泛型
				for (Entry<String, Object> entry : map.entrySet()) {
					re.put(entry.getKey(), convert(tt, entry.getValue()));
				}
			} else
				re.putAll(map);
			return re;
		} else { // 看来是Pojo
			JsonEntity jen = Json.getEntity(me.getType());
			Object re = jen.born();
			// 遍历目标对象的全部字段
			for (JsonEntityField jef : jen.getFields()) {
				Object value = map.get(jef.getName());
				if (value == null)
					continue;
				jef.setValue(re, jef.createValue(re, value));
			}
			return re;
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Object list2Object(Type type, List<Object> list) {
		Class<?> clazz = Lang.getTypeClass(type);
		Type tt = null;
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] ts = pt.getActualTypeArguments();
			if (ts != null && ts.length > 0)
				tt = Lang.getTypeClass(ts[0]);// TODO 只能做到一层的泛型
		}
		if (clazz.isArray()) {// 看来是数组
			List re = new ArrayList(list.size());
			for (Object object : list) {
				re.add(convert(clazz.getComponentType(), object));
			}
			Object ary = Array.newInstance(clazz.getComponentType(), list.size());
			int i = 0;

			for (Iterator it = re.iterator(); it.hasNext();) {
				if (tt != null)
					Array.set(ary, i++, convert(tt, it.next()));
				else
					Array.set(ary, i++, Castors.me().castTo(it.next(), clazz.getComponentType()));
			}
			return ary;
		}
		if (List.class.isAssignableFrom(clazz)) {
			if (tt == null) // 没有泛型信息? 那只好直接返回了
				return list;
			Mirror me = Mirror.me(clazz);
			List re = null;
			if (clazz.isInterface())
				re = new LinkedList();
			else
				re = (List) me.born();
			for (Object object : list) {
				re.add(convert(tt, object));
			}
			return re;
		}
		throw unexpectedType(List.class, clazz);
	}

	private static final RuntimeException unexpectedType(Type expect, Type act) {
		return Lang.makeThrow("expect %s but %s", expect, act);
	}
}
