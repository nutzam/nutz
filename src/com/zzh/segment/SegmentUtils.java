package com.zzh.segment;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.Mirror;

public class SegmentUtils {
	public static Segment fillSegmentByFields(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Mirror<?> me = Mirror.me(obj.getClass());
		Field[] fs = me.getFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			Object v = me.getValue(obj, f);
			if (v != null)
				seg.set(f.getName(), v);
			else
				seg.set(f.getName(), "");
		}
		return seg;
	}

	public static Segment fillSegmentByKeys(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Iterator<String> it = seg.keys().iterator();
		Class<?> klass = obj.getClass();
		Mirror<?> me = Mirror.me(klass);
		while (it.hasNext()) {
			String key = it.next();
			try {
				seg.set(key, me.getValue(obj, key));
			} catch (Exception e) {
				seg.set(key, "");
			}
		}
		return seg;
	}

	public static Segment fillSegmentByKeys(Segment seg, Map<String, ?> map) {
		if (null == map || null == seg)
			return seg;
		Iterator<String> it = seg.keys().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object v = map.get(key);
			seg.set(key, v);
		}
		return seg;
	}

}
