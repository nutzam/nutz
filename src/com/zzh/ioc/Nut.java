package com.zzh.ioc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zzh.ioc.json.JsonMappingLoader;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;

@SuppressWarnings("unchecked")
public class Nut implements Ioc {

	public Nut(CharSequence cs) {
		this(new JsonMappingLoader(Lang.inr(cs)));
	}

	public Nut(MappingLoader loader) {
		if (null == loader)
			Lang.makeThrow("Nut MappingLoader can NOT be null!!!");
		this.loader = loader;
		cache = new HashMap<String, ObjectHolder>();
		objectMappings = new HashMap<String, ObjectMapping<?>>();
		mappings = new HashMap<String, Mapping>();
		makers = new ArrayList<ObjectMaker>();
		add(new JavaObjectMaker()).add(new EvnObjectMaker()).add(new DiskFileMaker());
	}

	private Map<String, ObjectHolder> cache;
	private Map<String, ObjectMapping<?>> objectMappings;
	private Map<String, Mapping> mappings;
	private List<ObjectMaker> makers;
	private MappingLoader loader;
	private String[] keys;

	@Override
	public Ioc add(ObjectMaker maker) {
		makers.add(maker);
		return this;
	}

	@Override
	public <T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException {
		if (null == name)
			return null;
		ObjectHolder oh = cache.get(name);
		if (null != oh)
			return (T) oh.getObject();

		try {
			ObjectMapping<T> om = this.getObjectMapping(classOfT, name);
			oh = evalObjectMapping(name, om);
		} catch (ObjectNotFoundException e) {
			throw e;
		} catch (FailToMakeObjectException e) {
			throw e;
		} catch (Exception e) {
			throw new FailToMakeObjectException(name, e);
		}
		return (T) oh.getObject();
	}

	<T> ObjectHolder evalObjectMapping(String name, ObjectMapping<T> om) {
		ObjectHolder oh;
		oh = om.make();
		if (om.isSingleton())
			cache.put(name, oh);
		return oh;
	}

	<T> ObjectMapping<T> getObjectMapping(Class<T> classOfT, String name) throws Exception {
		ObjectMapping<T> im = (ObjectMapping<T>) objectMappings.get(name);
		if (null == im) {
			synchronized (this) {
				im = (ObjectMapping<T>) objectMappings.get(name);
				if (null == im) {
					Mapping mapping = loadMapping(name);
					Class<T> type = (Class<T>) mapping.getObjectType();
					if (null == type)
						if (null != classOfT)
							type = classOfT;
						else
							throw new FailToMakeObjectException(name, "object type is NULL");
					im = new ObjectMapping<T>(this, type, name, mapping);
					objectMappings.put(name, im);
				}
			}
		}
		return im;
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

	@Override
	public boolean isSingleton(Class<?> classOfT, String name) {
		try {
			return getObjectMapping(classOfT, name).isSingleton();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public ObjectMaker findMaker(Map<String, Object> properties) {
		for (Iterator<ObjectMaker> it = makers.iterator(); it.hasNext();) {
			ObjectMaker mk = it.next();
			if (mk.accept(properties))
				return mk;
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
			synchronized (this) {
				for (Iterator<ObjectHolder> it = this.cache.values().iterator(); it.hasNext();) {
					ObjectHolder ow = it.next();
					Deposer deposer = ow.getDeposer();
					if (deposer != null)
						deposer.depose(ow.getObject());
				}
				cache.clear();
				mappings.clear();
				objectMappings.clear();
			}
		}
		keys = null;
	}

	@Override
	public synchronized void depose() {
		clear();
		cache = null;
		mappings = null;
		objectMappings = null;
	}

	@Override
	protected void finalize() throws Throwable {
		depose();
	}
}
