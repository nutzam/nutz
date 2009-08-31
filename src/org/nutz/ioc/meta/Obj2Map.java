package org.nutz.ioc.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.nutz.castor.Castors;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public class Obj2Map {

	@SuppressWarnings("unchecked")
	public static Map<?, ?> render(Obj obj) {
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("id", obj.getId());
		map.put("comment", obj.getComment());
		map.put("name", obj.getName());
		map.put("type", obj.getType());
		map.put("singleton", obj.isSingleton());
		map.put("parent", obj.getParent());
		if (null != obj.getLifecycle()) {
			TreeMap<String, ?> lfMap = Castors.me().castTo(obj.getLifecycle(), TreeMap.class);
			if (obj.getLifecycle().getId() == 0)
				lfMap.remove("id");
			map.put("lifecycle", lfMap);
		}
		final List<Object> args = new LinkedList<Object>();
		Lang.each(obj.getArgs(), new Each<Object>() {
			public void invoke(int i, Object obj, int length) throws ExitLoop, LoopException {
				args.add(renderValueObject(obj));
			}
		});
		map.put("args", args);
		final Map<String, Object> fields = new TreeMap<String, Object>();
		Lang.each(obj.getFields(), new Each<Fld>() {
			public void invoke(int i, Fld fld, int length) throws ExitLoop, LoopException {
				fields.put(fld.getName(), renderValueObject(fld.getVal()));
			}
		});
		map.put("fields", fields);
		return map;
	}

	public static Object renderValueObject(Object obj) {
		if (null == obj)
			return null;
		if (obj instanceof Val) {
			return renderVal((Val) obj);
		} else if (obj instanceof Map<?, ?>) {
			return renderMap((Map<?, ?>) obj);
		} else if (obj instanceof Collection<?>) {
			return renderCollection((Collection<?>) obj);
		} else if (obj.getClass().isArray()) {
			return rednerArray((Object[]) obj);
		}
		return renderNormalValue(obj);
	}

	public static Object renderVal(Val v) {
		if (v.isNull())
			return null;
		else if (v.isInner()) {
			return render((Obj) v.getValue());
		} else if (v.isArray()) {
			if (v.getValue() instanceof Collection<?>)
				return renderCollection((Collection<?>) v.getValue());
			else
				return rednerArray((Object[]) v.getValue());
		} else if (v.isMap()) {
			return renderMap((Map<?, ?>) v.getValue());

		} else if (v.isBoolean()) {
			return Boolean.parseBoolean(v.getValue().toString());
		} else if (v.isNull() || null == v.getValue()) {
			return null;
		} else if (Val.SPECIAL.matcher(v.getType()).find()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put(v.getType(), v.getValue());
			return map;
		} else {
			return renderNormalValue(v.getValue());
		}
	}

	private static Object renderNormalValue(Object obj) {
		String s = obj.toString();
		if (Val.INT.matcher(s).find()) {
			int v = Integer.parseInt(s);
			if (s.equals(String.valueOf(v)))
				return v;
			return s;
		} else if (Val.FLOAT.matcher(s).find()) {
			long v = Long.parseLong(s);
			if (s.equals(String.valueOf(v)))
				return v;
			return s;
		}
		return s;
	}

	private static Map<?, ?> renderMap(Map<?, ?> map) {
		Map<Object, Object> nm = new TreeMap<Object, Object>();
		for (Object key : map.keySet()) {
			Object v = map.get(key);
			Object nv = renderValueObject(v);
			nm.put(key, nv);
		}
		return nm;
	}

	private static Collection<?> renderCollection(Collection<?> coll) {
		ArrayList<Object> list = new ArrayList<Object>(coll.size());
		for (Object o : coll) {
			list.add(renderValueObject(o));
		}
		return list;
	}

	private static Object[] rednerArray(Object[] objs) {
		if (null == objs)
			return null;
		Object[] array = new Object[objs.length];
		for (int i = 0; i < objs.length; i++)
			array[i] = renderValueObject(objs[i]);
		return array;
	}

}
