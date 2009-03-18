package com.zzh.ioc.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.ioc.Mapping;

public class ObjectMapping implements Mapping {

	public ObjectMapping(ObjectBean ob) {
		objectType = ob.getType();
		singleton = ob.isSingleton();
		borningArguments = new Object[ob.getArgs().size()];
		int i = 0;
		for (Iterator<ValueBean> it = ob.getArgs().iterator(); it.hasNext();) {
			ValueBean vb = it.next();
			borningArguments[i++] = vb.toValue();
		}
		fieldsSetting = new HashMap<String, Object>();
		for (Iterator<FieldBean> it = ob.getFields().iterator(); it.hasNext();) {
			FieldBean fb = it.next();
			if (fb.getValue() != null)
				fieldsSetting.put(fb.getName(), fb.getValue().toValue());
		}
	}

	private Class<?> objectType;
	private boolean singleton;
	private Map<String, Object> fieldsSetting;
	private Object[] borningArguments;

	@Override
	public Object[] getBorningArguments() {
		return borningArguments;
	}

	@Override
	public Map<String, Object> getFieldsSetting() {
		return fieldsSetting;
	}

	@Override
	public Class<?> getObjectType() {
		return objectType;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	@Override
	public void setSingleton(boolean sg) {
		this.singleton = sg;
	}

}
