package com.zzh.ioc.impl;

import java.util.HashMap;
import java.util.Map;

import com.zzh.ioc.Deposer;
import com.zzh.ioc.FailToMakeObjectException;
import com.zzh.ioc.Ioc;
import com.zzh.ioc.ObjLoader;
import com.zzh.ioc.ObjectNotFoundException;
import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.json.JsonLoader;
import com.zzh.ioc.maker.*;
import com.zzh.ioc.meta.Obj;
import com.zzh.ioc.meta.Val;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;

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
	}

	private ObjLoader loader;
	private Map<String, ObjectHolder<?>> cache;
	private Map<String, Obj> objs;
	private Map<String, ObjectCreator<?>> creators;
	private Map<String, ValueMaker> makers;

	@Override
	public void clear() {
		if (null != cache) {
			synchronized (this) {
				for (ObjectHolder oh : this.cache.values()) {
					Deposer deposer = oh.getDeposer();
					if (deposer != null)
						deposer.depose(oh.getObject());
				}
				cache.clear();
				objs.clear();
				creators.clear();
			}
		}
	}

	@Override
	public void depose() {
		clear();
		cache = null;
		objs = null;
		creators = null;
	}

	@Override
	protected void finalize() throws Throwable {
		depose();
	}

	@Override
	public <T> T get(Class<T> classOfT, String name) throws FailToMakeObjectException,
			ObjectNotFoundException {
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

	@Override
	public boolean isSingleton(Class<?> classOfT, String name) {
		return this.getCreator(classOfT, name).isSingleton();
	}

	@Override
	public String[] keys() {
		return loader.keys();
	}

	@Override
	public Ioc add(ValueMaker maker) {
		makers.put(maker.forType(), maker);
		return this;
	}

	@Override
	public ValueMaker findValueMaker(Val val) {
		return makers.get(val.getType());
	}

}
