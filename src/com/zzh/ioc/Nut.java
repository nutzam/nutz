package com.zzh.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.lang.Borning;
import com.zzh.lang.Each;
import com.zzh.lang.Invoking;
import com.zzh.lang.Lang;
import com.zzh.lang.LoopException;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

@SuppressWarnings("unchecked")
public class Nut implements Ioc {

	/**
	 * @author zozoh
	 * 
	 */
	static class ObjWrapper {

		ObjWrapper(Object obj, Deposer dep) {
			this.obj = obj;
			this.deposer = dep;
		}

		Object obj;
		Deposer deposer;
	}

	/**
	 * @author zozoh
	 * 
	 */
	static class MethodDeposer implements Deposer<Object> {

		private Method method;

		MethodDeposer(Method method) {
			this.method = method;
		}

		@Override
		public void depose(Object obj) {
			try {
				method.invoke(obj);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}

	}

	static RuntimeException failToMake(Exception e) {
		return Lang.wrapThrow(e);
	}

	/**
	 * @author zozoh
	 * 
	 * @param <T>
	 */
	static class InnerMapping<T> {

		private Mirror<T> mirror;
		private List<Injector> injectors;
		private Borning<T> borning;
		private Deposer deposer;
		private boolean singleton;

		public Deposer getDeposer() {
			return deposer;
		}

		public boolean isSingleton() {
			return singleton;
		}

		private <R> R makeValue(final Nut nut, Class<R> type, Object value) throws Exception {
			if (value instanceof Mapping) {
				return (new InnerMapping<R>(nut, type, (Mapping) value)).make();
			} else if (value instanceof Map) {
				Map<String, Object> map = (Map<String, Object>) value;
				// for @refer
				Object refer = map.get("refer");
				if (null != refer)
					return nut.get(type, refer.toString());
				// for customized ObjectMaker
				ObjectMaker<R> mk = nut.findMaker(type, map);
				if (null != mk)
					value = mk.make(map);
				else { // deep to loop all values
					for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
						String key = it.next();
						Object obj = map.get(key);
						obj = makeValue(nut, null, obj);
						map.put(key, obj);
					}
				}
			} else if (value instanceof Collection || value.getClass().isArray()) {
				final List<Object> list = new LinkedList<Object>();
				Lang.each(value, new Each<Object>() {
					public void invoke(int i, Object obj, int length) throws LoopException {
						try {
							Object re = makeValue(nut, null, obj);
							list.add(re);
						} catch (Exception e) {
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

		InnerMapping(final Nut nut, Class<T> classOfT, Mapping mapping) throws Exception {
			this.mirror = (Mirror<T>) Mirror.me(mapping.getObjectType());
			if (null == this.mirror)
				this.mirror = Mirror.me(classOfT);
			if (null == mirror)
				Lang.makeThrow("Don't know how to build InnerMapping!");
			singleton = mapping.isSingleton();
			// deposer
			if (!Strings.isBlank(mapping.getDeposeMethodName())) {
				Method m = classOfT.getMethod(mapping.getDeposeMethodName());
				deposer = new MethodDeposer(m);
			} else if (!Strings.isBlank(mapping.getDeposerTypeName())) {
				Class<?> depType = Class.forName(mapping.getDeposerTypeName());
				deposer = (Deposer) depType.newInstance();
			}
			// fields
			this.injectors = new LinkedList<Injector>();
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
				Object value = makeValue(nut, null, fss.get(key));
				Invoking invoking = null;
				try {
					try {
						invoking = mirror.getInvoking(Mirror.getSetterName(key), value);
					} catch (Exception e) {
						invoking = mirror.getInvoking(key, value);
					}
					injectors.add(new Injector.InvokingInjector(invoking));
				} catch (Exception e) {
					try {
						Field field = mirror.getField(key);
						injectors.add(new Injector.FieldInjector(field, value));
					} catch (Exception e1) {
						throw Lang.makeThrow(
								"Nut dont know how to inject [%s].%s by value [%s] because '%s'",
								mirror.getType().getName(), key, value, e1.getMessage());
					}
				}
			}
		}

		T make() {
			T obj = borning.born();
			for (Iterator<Injector> it = injectors.iterator(); it.hasNext();)
				it.next().inject(obj);
			return obj;
		}
	}

	static class MappingBean implements Mapping {

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

	/*
	 * ----------------------------------------------------
	 * ---------------------- --
	 */
	public Nut(MappingLoader loader) {
		if (null == loader)
			Lang.makeThrow("Nut MappingLoader   t be null!!!");
		this.loader = loader;
		cache = new HashMap<String, ObjWrapper>();
		innerMappings = new HashMap<String, InnerMapping<?>>();
		mappings = new HashMap<String, Mapping>();
		makers = new ArrayList<ObjectMaker<?>>();
		add(new JavaObjectMaker()).add(new EvnObjectMaker()).add(new NutObjectMaker(this));
	}

	private Map<String, ObjWrapper> cache;
	private Map<String, InnerMapping<?>> innerMappings;
	private Map<String, Mapping> mappings;
	private List<ObjectMaker<?>> makers;
	private MappingLoader loader;
	private String[] keys;

	public Nut add(ObjectMaker<?> maker) {
		makers.add(maker);
		return this;
	}

	@Override
	public <T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException {
		ObjWrapper ow = cache.get(name);
		if (null != ow)
			return (T) ow.obj;
		InnerMapping<T> im = (InnerMapping<T>) innerMappings.get(name);
		try {
			if (null == im) {
				synchronized (this) {
					im = (InnerMapping<T>) innerMappings.get(name);
					if (null == im) {
						Mapping mapping = loadMapping(name);
						Class<T> type = (Class<T>) mapping.getObjectType();
						if (null == type)
							if (null != classOfT)
								type = classOfT;
							else
								throw new FailToMakeObjectException(name, "object type is NULL");
						im = new InnerMapping<T>(this, type, mapping);
						innerMappings.put(name, im);
					}
				}
			} else {
				return (T) im.make();
			}
			Object obj = im.make();
			ow = new ObjWrapper(obj, im.getDeposer());
			if (im.isSingleton())
				cache.put(name, ow);

		} catch (ObjectNotFoundException e) {
			throw e;
		} catch (FailToMakeObjectException e) {
			throw e;
		} catch (Exception e) {
			throw new FailToMakeObjectException(name, e);
		}
		return (T) ow.obj;
	}

	private Mapping loadMapping(String name) {
		Mapping mapping = mappings.get(name);
		if (null == mapping)
			synchronized (this) {
				mapping = mappings.get(name);
				if (null == mapping) {
					try {
						mapping = loader.load(name);
					} catch (Exception e) {
						throw new ObjectNotFoundException(name, e);
					}
					if (null == mapping)
						throw new ObjectNotFoundException(name);
					// merge parent mapping
					if (!Strings.isBlank(mapping.getParentName())) {
						Mapping pm = loadMapping(mapping.getParentName());
						MappingBean mb = new MappingBean(pm);
						mapping = mb.merge(mapping);
					}
					// store
					mappings.put(name, mapping);
				}
			}
		return mapping;
	}

	private <T> ObjectMaker<T> findMaker(Class<T> type, Map<String, Object> properties) {
		for (Iterator<ObjectMaker<?>> it = makers.iterator(); it.hasNext();) {
			ObjectMaker<?> mk = (ObjectMaker<?>) it.next();
			if (mk.accept(properties))
				return (ObjectMaker<T>) mk;
		}
		return null;
	}

	@Override
	public String[] keys() {
		if (keys == null)
			keys = this.loader.keys();
		return keys;
	}

	@Override
	public void clear() {
		if (null != cache) {
			for (Iterator<ObjWrapper> it = this.cache.values().iterator(); it.hasNext();) {
				ObjWrapper ow = it.next();
				if (ow.deposer != null)
					ow.deposer.depose(ow.obj);
			}
			cache.clear();
			mappings.clear();
			innerMappings.clear();
		}
		keys = null;
	}

	@Override
	public void depose() {
		clear();
		cache = null;
		mappings = null;
		innerMappings = null;
	}

	@Override
	protected void finalize() throws Throwable {
		depose();
	}

}
