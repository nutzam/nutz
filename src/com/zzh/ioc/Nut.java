package com.zzh.ioc;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.Mirror;

public class Nut {

	public Nut(Assemble ass) {
		this.ass = ass;
	}

	private Assemble ass;
	private Map<String, Object> cache;

	public Object getObject(String name) {
		Mapping mapping = ass.getMapping(name);
		if (mapping.isSingleton()) {
			Object obj = cache.get(name);
			if (null == obj) {
				synchronized (this) {
					obj = cache.get(name);
					if (null == obj) {
						obj = makeObject(mapping);
						cache.put(name, obj);
					}
				}
			}
			return obj;
		}
		return makeObject(mapping);
	}

	private static Object makeObject(Mapping mapping) {
		Mirror<?> mirror = mapping.getMirror();
		Object obj = mapping.getMirror().born();
		for (Iterator<MappingField> it = mapping.getFields().iterator(); it.hasNext();) {
			MappingField mf = it.next();
			Field field = mf.getField();
			Object value = makeValue(mirror, obj, field, mf.getValue());
			mirror.setValue(obj, field, value);
		}
		return obj;
	}

	private static Object makeValue(Mirror<?> mirror, Object obj, Field field, MappingValue value) {
		// TODO Auto-generated method stub
		return null;
	}

}
