package com.zzh.ioc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.lang.born.Borning;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class ObjectMapping<T> {
	private Mirror<T> mirror;
	private List<Injector> injectors;
	private Borning<T> borning;
	private Deposer<T> deposer;
	private boolean singleton;

	public Deposer<T> getDeposer() {
		return deposer;
	}

	public boolean isSingleton() {
		return singleton;
	}

	@SuppressWarnings("unchecked")
	ObjectMapping(final Ioc ioc, Class<T> classOfT, String name, Mapping mapping) throws Exception {
		this.mirror = (Mirror<T>) Mirror.me(mapping.getObjectType());
		if (null == this.mirror)
			this.mirror = Mirror.me(classOfT);
		if (null == mirror)
			Lang.makeThrow("Don't know how to build InnerMapping!");
		singleton = mapping.isSingleton();
		// deposer
		if (!Strings.isBlank(mapping.getDeposeMethodName())) {
			Method m = classOfT.getMethod(mapping.getDeposeMethodName());
			deposer = (Deposer<T>) new MethodDeposer(m);
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
		Value.ArrayValue argValues = new Value.ArrayValue(ioc, name, objs);
		if (argValues.isDynamic()) {
			borning = new DynamicBorning<T>(mirror, argValues);
			singleton = false;
		} else
			borning = mirror.getBorning(argValues.getArray());
		// format fields value
		Map<String, Object> fss = mapping.getFieldsSetting();
		for (String key : fss.keySet()) {
			Value value = Value.make(ioc, null, name, fss.get(key));
			singleton &= !value.isDynamic();
			try {
				Object v = value.get();
				Class<?> paramType = null == v ? null : v.getClass();
				Method setter = mirror.getSetter(key, paramType);
				injectors.add(new Injector.SetterInjector(setter, value));
			} catch (FailToMakeObjectException e) {
				throw e;
			} catch (ObjectNotFoundException e) {
				throw e;
			} catch (Exception e) {
				try {
					Field field = mirror.getField(key);
					injectors.add(new Injector.FieldInjector(field, value));
				} catch (Exception e1) {
					throw new FailToMakeObjectException(String.format(
							"Nut dont know how to inject [%s].%s by value [%s] because '%s'",
							mirror.getType().getName(), key, value, e1.getMessage()));
				}
			}
		}
	}

	ObjectHolder<T> make() {
		T obj = borning.born();
		for (Injector inj : injectors)
			inj.inject(obj);
		return new ObjectHolder<T>(obj, getDeposer());
	}
}
