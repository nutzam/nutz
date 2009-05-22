package com.zzh.ioc.meta;

import java.util.Map;
import java.util.TreeMap;

import com.zzh.ioc.Mapping;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;

public class MappingBean implements Mapping {

	public MappingBean(Obj obj) {
		singleton = obj.isSingleton();
		if (!Strings.isBlank(obj.getType()))
			try {
				objectType = Class.forName(obj.getType());
			} catch (ClassNotFoundException e) {
				throw Lang.wrapThrow(e);
			}
		parentName = obj.getParent();
		deposeMethodName = obj.getDeposeby();
		deposerTypeName = obj.getDeposer();
		if (null != obj.getArgs()) {
			borningArguments = new Object[obj.getArgs().length];
			int i = 0;
			for (Val val : obj.getArgs()) {
				borningArguments[i++] = Obj2Map.renderVal(val);
			}
		} else {
			borningArguments = new Object[0];
		}
		fieldsSetting = new TreeMap<String, Object>();
		if (null != obj.getFields())
			for (Fld f : obj.getFields()) {
				fieldsSetting.put(f.getName(), Obj2Map.renderVal(f.getVal()));
			}
	}

	private Class<?> objectType;
	private boolean singleton;
	private Map<String, Object> fieldsSetting;
	private Object[] borningArguments;
	private String parentName;
	private String deposeMethodName;
	private String deposerTypeName;

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
		return parentName;
	}

}
