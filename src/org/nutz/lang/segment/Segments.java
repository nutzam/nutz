package org.nutz.lang.segment;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.nutz.lang.Mirror;

public class Segments {
	public static Segment fillByFields(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Mirror<?> mirror = Mirror.me(obj.getClass());
		// Primitive Type: set it to all PlugPoints
		if (mirror.isStringLike() || mirror.isBoolean() || mirror.isNumber() || mirror.isChar()) {
			for (Field f : mirror.getFields())
				seg.set(f.getName(), obj.toString());
		}
		// Map: set by key
		else if (mirror.isOf(Map.class)) {
			Map<?, ?> map = (Map<?, ?>) obj;
			for (Iterator<?> it = map.keySet().iterator(); it.hasNext();) {
				Object key = it.next();
				seg.set(key.toString(), map.get(key));
			}
		}
		// POJO: set by field
		else {
			for (Field f : mirror.getFields()) {
				Object v = mirror.getValue(obj, f);
				if (v != null)
					seg.set(f.getName(), v);
				else
					seg.set(f.getName(), "");
			}
		}
		return seg;
	}

	public static Segment fillByKeys(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Iterator<String> it = seg.keys().iterator();
		Class<?> klass = obj.getClass();
		Mirror<?> mirror = Mirror.me(klass);
		// Primitive Type: set it to all PlugPoints
		if (mirror.isStringLike() || mirror.isBoolean() || mirror.isNumber() || mirror.isChar()) {
			seg.setAll(obj);
		}
		// Map: set by key
		else if (mirror.isOf(Map.class)) {
			Map<?, ?> map = (Map<?, ?>) obj;
			while (it.hasNext()) {
				String key = it.next();
				try {
					seg.set(key, map.get(key));
				} catch (Exception e) {
					seg.set(key, "");
				}
			}
		}
		// POJO: set by field
		else {
			while (it.hasNext()) {
				String key = it.next();
				try {
					seg.set(key, mirror.getValue(obj, key));
				} catch (Exception e) {
					seg.set(key, "");
				}
			}
		}
		return seg;
	}
}
