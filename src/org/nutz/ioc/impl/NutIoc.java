package org.nutz.ioc.impl;

import java.util.HashMap;
import java.util.Map;

import org.nutz.ioc.FailToMakeObjectException;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjLoader;
import org.nutz.ioc.ObjectNotFoundException;
import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.aop.MirrorFactory;
import org.nutz.ioc.json.JsonLoader;
import org.nutz.ioc.maker.*;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

@SuppressWarnings("unchecked")
public class NutIoc implements Ioc {

	public NutIoc(CharSequence cs) {
		this(new JsonLoader(Lang.inr(cs)));
	}

	public NutIoc(ObjLoader loader) {
		this.loader = loader;
		objs = new HashMap<String, Obj>();
		creators = new HashMap<String, ObjectCreator<?>>();
		cache = new HashMap<String, ObjectHolder<?>>();
		makers = new HashMap<String, ValueMaker>();
		add(new JavaObjectMaker()).add(new EvnObjectMaker()).add(new DiskFileMaker());
		mirrors = new MirrorFactory();
		mirrors.init(this, "$aop");
	}

	private ObjLoader loader;
	private MirrorFactory mirrors;
	private Map<String, ObjectHolder<?>> cache;
	private Map<String, Obj> objs;
	private Map<String, ObjectCreator<?>> creators;
	private Map<String, ValueMaker> makers;
	private AopSetting as;

	AopSetting aop() {
		return as;
	}

	MirrorFactory mirrors() {
		return mirrors;
	}

	public void clear() {
		if (null != cache) {
			synchronized (this) {
				for (ObjectHolder oh : this.cache.values())
					oh.depose();
				cache.clear();
				objs.clear();
				creators.clear();
			}
		}
	}

	public void depose() {
		clear();
		cache = null;
		objs = null;
		creators = null;
	}

	protected void finalize() throws Throwable {
		depose();
	}

	public <T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException, ObjectNotFoundException {
		if (null == name)
			return null;
		ObjectHolder<T> oh = (ObjectHolder<T>) cache.get(name);
		if (null != oh)
			return oh.getObject();

		try {
			ObjectCreator<T> creator = this.getCreator(classOfT, name);
			oh = creator.make();
			if (creator.isSingleton())
				cache.put(name, oh);
		} catch (ObjectNotFoundException e) {
			throw e;
		} catch (FailToMakeObjectException e) {
			throw e;
		} catch (Exception e) {
			throw new FailToMakeObjectException(name, e);
		}
		return oh.getObject();
	}

	<T> ObjectCreator<T> getCreator(Class<T> classOfT, String name) {
		if (!hasName(name)) {
			throw new ObjectNotFoundException(name);
		}
		ObjectCreator<T> creator = (ObjectCreator<T>) creators.get(name);
		if (null == creator) {
			synchronized (this) {
				creator = (ObjectCreator<T>) creators.get(name);
				if (null == creator) {
					Obj obj = load(name);
					creator = new ObjectCreator<T>(this, classOfT, obj);
					creators.put(name, creator);
				}
			}
		}
		return creator;
	}

	private Obj load(String name) {
		Obj obj = objs.get(name);
		if (null == obj)
			synchronized (this) {
				obj = objs.get(name);
				if (null == obj) {
					try {
						obj = loader.load(name);
					} catch (Exception e) {
						throw new ObjectNotFoundException(name, e);
					}
					if (null == obj)
						throw new ObjectNotFoundException(name);
					// merge parent mapping
					if (!Strings.isBlank(obj.getParent())) {
						Obj parent = load(obj.getParent());
						obj = Utils.merge(obj, parent);
					}
					// store
					objs.put(name, obj);
				}
			}
		return obj;
	}

	public boolean isSingleton(Class<?> classOfT, String name) {
		return this.getCreator(classOfT, name).isSingleton();
	}

	public String[] keys() {
		return loader.keys();
	}

	public boolean hasName(String name) {
		if (cache.containsKey(name))
			return true;
		if (creators.containsKey(name))
			return true;
		return loader.hasObj(name);
	}

	public Ioc add(ValueMaker maker) {
		makers.put(maker.forType(), maker);
		return this;
	}

	public ValueMaker findValueMaker(Val val) {
		return makers.get(val.getType());
	}

}
