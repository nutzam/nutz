package com.zzh.ioc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Design for "extends"
 */
class MappingBean implements Mapping {

	public MappingBean(Mapping mapping) {
		objectType = mapping.getObjectType();
		singleton = mapping.isSingleton();
		fieldsSetting = new HashMap<String, Object>();
		fieldsSetting.putAll(mapping.getFieldsSetting());
		borningArguments = mapping.getBorningArguments();
		deposeMethodName = mapping.getDeposeMethodName();
		deposerTypeName = mapping.getDeposerTypeName();
	}

	private Class<?> objectType;
	private boolean singleton;
	private Map<String, Object> fieldsSetting;
	private Object[] borningArguments;
	private String deposeMethodName;
	private String deposerTypeName;

	Mapping merge(Mapping m) {
		if (null != m.getObjectType())
			objectType = m.getObjectType();
		singleton = m.isSingleton();
		if (null != m.getBorningArguments())
			borningArguments = m.getBorningArguments();
		if (null != m.getDeposeMethodName())
			deposeMethodName = m.getDeposeMethodName();
		if (null != m.getDeposerTypeName())
			deposerTypeName = m.getDeposerTypeName();
		for (Iterator<String> it = m.getFieldsSetting().keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object value = m.getFieldsSetting().get(key);
			fieldsSetting.put(key, value);
		}
		return this;
	}

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

	@Override
	public String getDeposeMethodName() {
		return deposeMethodName;
	}

	@Override
	public String getDeposerTypeName() {
		return deposerTypeName;
	}

	@Override
	public String getParentName() {
		return null;
	}

}