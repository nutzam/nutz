package com.zzh.ioc.db;

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

	ObjectMapping(ObjectBean bean) {
		name = bean.getName();
		type = bean.getType();
		singleton = bean.isSingleton();
		fields = new HashMap<Field, Value>();
		Map<Integer, Value> args = new HashMap<Integer, Value>();
		try {
			for (Iterator<FieldBean> it = bean.getFields().iterator(); it.hasNext();) {
				FieldBean fb = it.next();
				String fname = fb.getName();
				if (fname.startsWith(Nut.PREFIX_ARG)) {
					args.put(Integer.valueOf(fname.substring(Nut.PREFIX_ARG.length())), makeValue(fb
							.getValue(), Nut.PREFIX_REFER));
				} else {
					Field field = type.getField(fname);
					ObjectValue of = makeValue(fb.getValue(), Nut.PREFIX_REFER);
					fields.put(field, of);
				}
			}
			if (args.size() > 0) {
				constructorArguments = new Value[args.size()];
				for (Iterator<Integer> it = args.keySet().iterator(); it.hasNext();) {
					int i = it.next();
					constructorArguments[i] = args.get(i);
				}
			}
		} catch (NoSuchFieldException e) {
			throw Lang.wrapThrow(e, FailToMakeObjectException.class);
		}
	}

	private static ObjectValue makeValue(String value, String referPrefix) {
		ObjectValue of = new ObjectValue();
		if (!Strings.isBlank(value) && value.startsWith(referPrefix)) {
			of.referName = Strings.trim(value.substring(referPrefix.length()));
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
