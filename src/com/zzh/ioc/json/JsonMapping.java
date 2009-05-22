package com.zzh.ioc.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.ioc.Mapping;
import com.zzh.lang.Each;
import com.zzh.lang.Lang;
import com.zzh.lang.LoopException;

public class JsonMapping implements Mapping {

	@SuppressWarnings("unchecked")
	public JsonMapping(Map<String, Object> map) {
		fieldsSetting = new HashMap<String, Object>();
		singleton = true;
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object value = map.get(key);
			if (null == value)
				continue;
			if ("type".equals(key)) {
				try {
					objectType = Class.forName(value.toString());
				} catch (ClassNotFoundException e) {
					throw Lang.wrapThrow(e);
				}
			} else if ("singleton".equals(key)) {
				try {
					singleton = (Boolean) value;
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			} else if ("extends".equals(key)) {
				this.parentName = value.toString();
			} else if ("deposeby".equals(key)) {
				this.deposeMethodName = value.toString();
			} else if ("deposer".equals(key)) {
				this.deposerTypeName = value.toString();
			} else if ("args".equals(key)) {
				Collection<Object> coll = (Collection<Object>) value;
				borningArguments = new Object[coll.size()];
				int i = 0;
				for (Iterator<Object> oit = coll.iterator(); oit.hasNext();) {
					borningArguments[i++] = makeValue(oit.next());
				}
			} else if ("fields".equals(key)) {
				Map<String, Object> fm = (Map<String, Object>) value;
				for (Iterator<String> fi = fm.keySet().iterator(); fi.hasNext();) {
					String name = fi.next();
					Object fv = fm.get(name);
					Object vv = makeValue(fv);
					fieldsSetting.put(name, vv);
				}
			}
		}
		if (singleton && null == objectType && null == parentName && null == deposeMethodName
				&& null == deposerTypeName && null == borningArguments && 0 == fieldsSetting.size()) {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String name = it.next();
				Object fv = map.get(name);
				Object vv = makeValue(fv);
				fieldsSetting.put(name, vv);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static Object makeValue(Object fv) {
		if (null == fv)
			return null;
		if ((fv instanceof Map) && ((Map) fv).containsKey("type"))
			return new JsonMapping((Map<String, Object>) fv);
		else if (fv instanceof Collection || fv.getClass().isArray()) {
			final List<Object> list = new LinkedList<Object>();
			Lang.each(fv, new Each<Object>() {
				public void invoke(int i, Object obj, int length) throws LoopException {
					Object re = makeValue(obj);
					list.add(re);
				}
			});
			return list;
		}
		return fv;
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
