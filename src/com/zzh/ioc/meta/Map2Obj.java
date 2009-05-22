package com.zzh.ioc.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;

public class Map2Obj {

	private static final Pattern OBJFIELDS = Pattern
			.compile("^(id|comment|parent|name|type|singleton|fields|args|deposeby|deposer)$");

	public static boolean isObj(Map<?, ?> map) {
		for (Object key : map.keySet()) {
			if (!OBJFIELDS.matcher(key.toString()).find())
				return false;
		}
		return true;
	}

	public static boolean isVal(Map<?, ?> map) {
		return map.containsKey("id") && map.containsKey("type") && map.containsKey("value");
	}

	public static Obj parse(Map<?, ?> map) {
		if (!isObj(map)) {
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("fields", map);
			return parse(newMap);
		}
		Obj obj = new Obj();
		try {
			obj.setId(Integer.parseInt(map.get("id").toString()));
		} catch (Exception e) {}
		obj.setName((String) map.get("name"));
		obj.setType((String) map.get("type"));
		obj.setComment((String) map.get("comment"));
		if (map.containsKey("singleton"))
			obj.setSingleton((Boolean) map.get("singleton"));
		else
			obj.setSingleton(true);
		obj.setParent((String) map.get("parent"));
		obj.setDeposer((String) map.get("deposer"));
		obj.setDeposeby((String) map.get("deposeby"));
		List<?> objs = (List<?>) map.get("args");
		if (null != objs) {
			Val[] args = new Val[objs.size()];
			int i = 0;
			for (Object o : objs) {
				args[i++] = object2val(o);
			}
			obj.setArgs(args);
		}
		Map<?, ?> fldMap = (Map<?, ?>) map.get("fields");
		int i = 0;
		if (null != fldMap) {
			Fld[] flds = new Fld[fldMap.size()];
			for (Object key : fldMap.keySet()) {
				Object value = fldMap.get(key);
				Fld fld = new Fld();
				fld.setName(key.toString());
				fld.setVal(object2val(value));
				flds[i++] = fld;
			}
			obj.setFields(flds);
		}
		return obj;
	}

	public static Val object2val(Object obj) {
		if (null == obj) {
			return Val.make(Val.Null, null);
		} else if (obj instanceof Map) {
			return map2val((Map<?, ?>) obj);
		} else if (obj instanceof Collection) {
			return Val.make(Val.array, parseCollection((Collection<?>) obj));
		} else if (obj.getClass().isArray()) {
			return Val.make(Val.array, parseArray((Object[]) obj));
		}
		Mirror<?> mirror = Mirror.me(obj.getClass());
		if (mirror.isBoolean())
			return Val.make(Val.bool, obj);
		return Val.make(Val.normal, obj);
	}

	public static Val map2val(Map<?, ?> map) {
		if (isObj(map)) {
			return Val.make(Val.inner, parse(map));
		} else if (map.size() == 1) {
			String key = map.keySet().iterator().next().toString();
			if (Val.SPECIAL.matcher(key).find()) {
				return Val.make(key, map.get(key));
			} else {
				return Val.make(Val.map, parseMap(map));
			}
		} else {
			return Val.make(Val.map, parseMap(map));
		}
	}

	private static Object deeply_check_object_and_return_self_when_normal(Object obj) {
		if (null == obj)
			return null;
		if (obj instanceof Map) {
			Val val = map2val((Map<?, ?>) obj);
			if (val.isMap())
				return val.getValue();
			else
				return val;
		} else if (obj instanceof Collection<?>) {
			return parseCollection((Collection<?>) obj);
		} else if (obj.getClass().isArray()) {
			return parseArray((Object[]) obj);
		}
		return obj;
	}

	public static Map<?, ?> parseMap(Map<?, ?> map) {
		Map<Object, Object> nm = new TreeMap<Object, Object>();
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			if (null == value)
				continue;
			Object v = deeply_check_object_and_return_self_when_normal(value);
			nm.put(key, v);
		}
		return nm;
	}

	public static Object[] parseArray(Object[] obj) {
		if (null == obj)
			return null;
		Object[] array = new Object[obj.length];
		for (int i = 0; i < obj.length; i++) {
			Object value = obj[i];
			if (null == value)
				continue;
			Object v = deeply_check_object_and_return_self_when_normal(value);
			array[i] = v;
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static Object[] parseCollection(Collection<?> coll) {
		return parseArray(Lang.collection2array((Collection<Object>) coll, Object.class));
	}

}
