package com.zzh.lang.segment;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.Mirror;

public class Segments {
	public static Segment fillSegmentByFields(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Mirror<?> me = Mirror.me(obj.getClass());
		if (me.isOf(Map.class)) {
			Map<?, ?> map = (Map<?, ?>) obj;
			for(Iterator<?> it = map.keySet().iterator();it.hasNext();){
				Object key = it.next();
				seg.set(key.toString(), map.get(key));
			}
		} else {
			Field[] fs = me.getFields();
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				Object v = me.getValue(obj, f);
				if (v != null)
					seg.set(f.getName(), v);
				else
					seg.set(f.getName(), "");
			}
		}
		return seg;
	}

	public static Segment fillSegmentByKeys(Segment seg, Object obj) {
		if (null == obj || null == seg)
			return seg;
		Iterator<String> it = seg.keys().iterator();
		Class<?> klass = obj.getClass();
		Mirror<?> me = Mirror.me(klass);
		if (me.isOf(Map.class)) {
			Map<?, ?> map = (Map<?, ?>) obj;
			while (it.hasNext()) {
				String key = it.next();
				try {
					seg.set(key, map.get(key));
				} catch (Exception e) {
					seg.set(key, "");
				}
			}
		} else {
			while (it.hasNext()) {
				String key = it.next();
				try {
					seg.set(key, me.getValue(obj, key));
				} catch (Exception e) {
					seg.set(key, "");
				}
			}
		}
		return seg;
	}
}
