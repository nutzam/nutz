package com.zzh.ioc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Borning;
import com.zzh.lang.Each;
import com.zzh.lang.Lang;
import com.zzh.lang.LoopException;
import com.zzh.lang.Mirror;

@SuppressWarnings("unchecked")
public class Nut implements Ioc {

	static RuntimeException failToMake(Exception e) {
		return Lang.wrapThrow(e);
	}

	static class InnerMapping<T> {

		private Mirror<T> mirror;
		private Map<String, Object> fields;
		private Borning<T> borning;

		private <R> R makeValue(final Nut nut, Class<R> type, Object value)
				throws FailToCastObjectException {
			if (value instanceof Mapping) {
				return (new InnerMapping<R>(nut, type, (Mapping) value)).make();
			} else if (value instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) value;
				// for @refer
				Object refer = map.get("refer");
				if (null != refer)
					return nut.getObject(type, refer.toString());
				// for customized ObjectMaker
				ObjectMaker<R> mk = nut.findMaker(type, map);
				if (null != mk)
					value = mk.make(map);
			} else if (value instanceof Collection || value.getClass().isArray()) {
				final List<Object> list = new LinkedList<Object>();
				Lang.each(value, new Each<Object>() {
					public void invoke(int i, Object obj, int length) throws LoopException {
						try {
							Object re = makeValue(nut, null, obj);
							list.add(re);
						} catch (FailToCastObjectException e) {
							throw new LoopException(e);
						}
					}
				});
				value = list;
			}
			if (null == type)
				return (R) value;
			return Castors.me().castTo(value, type);
		}

		InnerMapping(final Nut nut, Class<T> classOfT, Mapping mapping)
				throws FailToCastObjectException {
			this.mirror = (Mirror<T>) Mirror.me(mapping.getObjectType());
			if (null == this.mirror)
				this.mirror = Mirror.me(classOfT);
			if (null == mirror)
				Lang.makeThrow("Don't know how to build InnerMapping!");
			this.fields = new HashMap<String, Object>();
			// format the constructor arguments
			Object[] objs = mapping.getBorningArguments();
			if (null == objs)
				objs = new Object[0];
			final Object[] args = new Object[objs.length];
			for (int i = 0; i < objs.length; i++) {
				Object arg = objs[i];
				args[i] = makeValue(nut, null, arg);
			}
			borning = mirror.getBorning(args);
			// format fields value
			Map<String, Object> fss = mapping.getFieldsSetting();
			for (Iterator<String> it = fss.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = fss.get(key);
				Class<?> type = null;

				Method[] setters = mirror.findSetters(key);
				if (setters.length > 0) {
					type = setters[0].getParameterTypes()[0];
					for (int i = 1; i < setters.length; i++) {
						if (setters[i].getParameterTypes()[0] == value.getClass())
							type = value.getClass();
					}
				}
				try {
					if (null == type)
						type = mirror.getField(key).getType();
				} catch (NoSuchFieldException e) {}
				this.fields.put(key, makeValue(nut, type, value));
			}
		}

		T make() {
			T obj = null;
			if (null != obj)
				return obj;
			obj = borning.born();
			for (Iterator<String> it = fields.keySet().iterator(); it.hasNext();) {
				String name = it.next();
				Object value = fields.get(name);
				mirror.setValue(obj, name, value);
			}
			return obj;
		}
	}

	public Nut(MappingLoader loader) {
		if (null == loader)
			Lang.makeThrow("Nut MappingLoader can not be null!!!");
		this.loader = loader;
		cache = new HashMap<String, Object>();
		mappings = new HashMap<String, InnerMapping<?>>();
		makers = new ArrayList<ObjectMaker<?>>();
		deposers = new ArrayList<Deposer>();
		add(new JavaObjectMaker()).add(new EvnObjectMaker()).add(new NutObjectMaker(this));
	}

	private Map<String, Object> cache;
	private Map<String, InnerMapping<?>> mappings;
	private List<ObjectMaker<?>> makers;
	private List<Deposer> deposers;
	private MappingLoader loader;

	public Nut add(ObjectMaker<?> maker) {
		makers.add(maker);
		return this;
	}

	@Override
	public <T> T getObject(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException {
		T obj = (T) cache.get(name);
		if (null != obj)
			return obj;
		InnerMapping<T> im = (InnerMapping<T>) mappings.get(name);
		try {
			if (null == im) {
				synchronized (this) {
					im = (InnerMapping<T>) mappings.get(name);
					if (null == im) {
						Mapping mapping;
						try {
							mapping = loader.load(name);
						} catch (Exception e) {
							throw new ObjectNotFoundException(name, e);
						}
						if (null == mapping)
							throw new ObjectNotFoundException(name);
						Class<T> type = (Class<T>) mapping.getObjectType();
						if (null == type)
							if (null != classOfT)
								type = classOfT;
							else
								throw new FailToMakeObjectException(name, "object type is NULL");
						im = new InnerMapping<T>(this, type, mapping);
						obj = im.make();
						if (mapping.isSingleton())
							cache.put(name, obj);
						else
							mappings.put(name, im);
					}
				}
			} else {
				obj = im.make();
			}
		} catch (ObjectNotFoundException e) {
			throw e;
		} catch (FailToMakeObjectException e) {
			throw e;
		} catch (Exception e) {
			throw new FailToMakeObjectException(name, e);
		}
		return obj;
	}

	private <T> ObjectMaker<T> findMaker(Class<T> type, Map<String, Object> properties) {
		for (Iterator<ObjectMaker<?>> it = makers.iterator(); it.hasNext();) {
			ObjectMaker<?> mk = (ObjectMaker<?>) it.next();
			if (mk.accept(properties))
				return (ObjectMaker<T>) mk;
		}
		return null;
	}

	public void clearCache() {
		this.cache.clear();
		this.mappings.clear();
	}

	@Override
	public Ioc addDeposer(Deposer deposer) {
		deposers.add(deposer);
		return this;
	}

	@Override
	public void depose() {
		for (Iterator<Deposer> it = deposers.iterator(); it.hasNext();) {
			it.next().depose(this);
		}
	}

}
