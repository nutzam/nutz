package com.zzh.ioc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror;

public class Nut {
	final public static String PREFIX_ARG = "$";
	final public static String PREFIX_REFER = "->";

	public Nut() {
		this.cache = new HashMap<String, Object>();
		this.castors = Castors.me();
	}

	public Nut(Assemble ass, Castors castors) {
		this.cache = new HashMap<String, Object>();
		this.ass = ass;
		this.castors = castors;
	}

	private Castors castors;
	private Assemble ass;

	public synchronized void setAss(Assemble ass) {
		this.clearCache();
		this.ass = ass;
	}

	public void setCastors(Castors castors) {
		this.castors = castors;
	}

	public synchronized void clearCache() {
		this.cache.clear();
	}

	private Map<String, Object> cache;

	public Object getObject(String name) throws ObjectNotFoundException {
		Object obj = cache.get(name);
		if (null == obj) {
			synchronized (this) {
				obj = cache.get(name);
				if (null == obj) {
					Mapping mapping = ass.getMapping(name);
					if (null == mapping)
						throw new ObjectNotFoundException(name);
					if (mapping.isSingleton())
						obj = makeObject(mapping);
					else
						obj = mapping;
					cache.put(name, obj);
				}
			}
		}
		return obj instanceof Mapping ? makeObject((Mapping) obj) : obj;

	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> classOfT, String name) throws ObjectNotFoundException {
		return (T) getObject(name);
	}

	private Object makeObject(Mapping mapping) throws ObjectNotFoundException {
		Mirror<?> mirror = mapping.getType();
		Value[] avs = mapping.getConstructorArguments();
		Object obj;
		if (null == avs || avs.length == 0)
			obj = mirror.born();
		else {
			Object[] args = new Object[avs.length];
			for (int i = 0; i < avs.length; i++) {
				args[i] = (null == avs[i].getReferName() ? avs[i].getValue() : this
						.getObject(avs[i].getReferName()));
			}
			obj = mirror.born(args);
		}
		Map<Field, Value> fields = mapping.getMappingFields();
		try {
			for (Iterator<Field> it = fields.keySet().iterator(); it.hasNext();) {
				Field field = it.next();
				Value fv = fields.get(field);
				Object value;
				String referName = fv.getReferName();
				if (null != referName) {
					value = this.getObject(referName);
				} else {
					Object vObj = fv.getValue();
					if (vObj instanceof Mapping)
						value = makeObject((Mapping) vObj);
					else
						value = castors.cast(vObj, vObj.getClass(), field.getType());
				}
				mirror.setValue(obj, field, value);
			}
		} catch (FailToCastObjectException e) {
			throw new FailToMakeObjectException(e);
		}
		return obj;
	}
}
