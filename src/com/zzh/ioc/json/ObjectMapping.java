package com.zzh.ioc.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.ioc.FailToMakeObjectException;
import com.zzh.ioc.Nut;
import com.zzh.ioc.Value;
import com.zzh.ioc.Mapping;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class ObjectMapping implements Mapping {

	private String name;
	private Mirror<?> type;
	private boolean singleton;
	private Map<Field, Value> fields;
	private Value[] constructorArguments;

	@SuppressWarnings("unchecked")
	ObjectMapping(String name, Map<String, Object> map) {
		this.name = name;
		try {
			type = Mirror.me(Class.forName(map.get("type").toString()));
			singleton = (Boolean) map.get("singleton");
			fields = new HashMap<Field, Value>();
			Map<String, Object> fieldsMap = (Map<String, Object>) map.get("fields");
			for (Iterator<String> it = fieldsMap.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = fieldsMap.get(key);
				if (null == value)
					continue;
				ObjectValue makedValue = makeValue(value, Nut.PREFIX_REFER);
				Field field = type.getField(key);
				ObjectValue of = makedValue;
				fields.put(field, of);
			}
			Object[] args = (Object[]) map.get("new");
			if (null != args) {
				constructorArguments = new Value[args.length];
				for (int i = 0; i < args.length; i++) {
					if (args[i] == null)
						constructorArguments[i] = null;
					else
						constructorArguments[i] = makeValue(args[i], Nut.PREFIX_REFER);
				}
			}

		} catch (Exception e) {
			throw Lang.wrapThrow(e, FailToMakeObjectException.class);
		}
	}

	@SuppressWarnings("unchecked")
	private static ObjectValue makeValue(Object value, String referPrefix) {
		ObjectValue of = new ObjectValue();
		if (value instanceof CharSequence) {
			String vs = value.toString();
			if (!Strings.isBlank(vs) && vs.startsWith(referPrefix))
				of.referName = Strings.trim(vs.substring(referPrefix.length()));
			else
				of.value = vs;
		} else if (value instanceof Map) {
			of.value = new ObjectMapping(null, (Map<String, Object>) value);
		} else {
			of.value = value;
		}
		return of;
	}

	@Override
	public Map<Field, Value> getMappingFields() {
		return fields;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Mirror<?> getType() {
		return type;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	@Override
	public Value[] getConstructorArguments() {
		return constructorArguments;
	}

}
