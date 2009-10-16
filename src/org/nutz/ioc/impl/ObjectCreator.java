package org.nutz.ioc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.ObjCallback;
import org.nutz.ioc.impl.DynamicBorning;
import org.nutz.ioc.Events;
import org.nutz.ioc.FailToMakeObjectException;
import org.nutz.ioc.MethodCallback;
import org.nutz.ioc.ObjectNotFoundException;
import org.nutz.ioc.meta.Fld;
import org.nutz.ioc.meta.Obj;
import org.nutz.ioc.meta.Val;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;

public class ObjectCreator<T> {

	@SuppressWarnings("unchecked")
	Mirror<T> evalMirror(Class<T> classOfT, Obj obj) {
		if (!Strings.isBlank(obj.getType()))
			try {
				classOfT = (Class<T>) Class.forName(obj.getType());
			} catch (Exception e) {}
		if (null != ioc.aop())
			this.ca = ioc.aop().evalClassAgent(classOfT, obj.getName());
		if (null != ca)
			classOfT = ca.define(classOfT);
		if (null != classOfT)
			return Mirror.me(classOfT);
		throw Lang.makeThrow("Fail eval Mirror<%s> in ObjectCreator for object [%s]!",
				(null == classOfT ? "?" : classOfT.getName()), obj.getName());
	}

	/*-----------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	private static <T> Events<T> evalLifecycle(Class<T> classOfT, Obj obj) {
		if (null == obj.getLifecycle())
			return null;
		String name = null;
		Events es = new Events();
		try {
			name = "create";
			es.setWhenCreate(evalCallback(classOfT, obj.getLifecycle().getCreate()));
			name = "depose";
			es.setWhenDepose(evalCallback(classOfT, obj.getLifecycle().getDepose()));
			name = "fetch";
			es.setWhenFetch(evalCallback(classOfT, obj.getLifecycle().getFetch()));
		} catch (Exception e) {
			throw Lang.makeThrow("For [%s]: Can not create lifecycle '[%s]', because:\n%s", obj
					.getName(), name, e.getMessage());
		}
		return es;
	}

	@SuppressWarnings("unchecked")
	private static <T> ObjCallback<T> evalCallback(Class<T> classOfT, String name) throws Exception {
		if (Strings.isBlank(name))
			return null;
		if (!name.contains(".")) {
			try {
				Method m = classOfT.getMethod(name);
				return (ObjCallback<T>) new MethodCallback(m);
			} catch (Exception e) {}
		}
		Class<?> callbackType = Class.forName(name);
		return (ObjCallback<T>) callbackType.newInstance();
	}

	/*-----------------------------------------------------------------------------*/
	private void evalBorning() {
		Val[] args = obj.getArgs();
		if (null == args)
			args = new Val[0];
		boolean argumentsIsDynamice = false;
		ValueDelegate[] vds = new ValueDelegate[args.length];
		// maybe I should give each argument a type, not only send null to
		// eval()
		for (int i = 0; i < args.length; i++) {
			vds[i] = ValueDelegate.eval(ioc, obj.getName(), null, args[i]);
			if (vds[i].isDynamic())
				argumentsIsDynamice = true;
		}
		singleton &= !argumentsIsDynamice;
		borning = new DynamicBorning<T>(mirror, vds);
		if (!argumentsIsDynamice)
			borning = ((DynamicBorning<T>) borning).toStatic();
	}

	/*-----------------------------------------------------------------------------*/
	private void evalFieldInjectors() {
		if (null != obj.getFields()) {
			injectors = new ArrayList<Injector>(obj.getFields().length);
			for (Fld fld : obj.getFields()) {
				Class<?> fieldType = null;
				// try to find the type
				try {
					Field field = mirror.getField(fld.getName());
					fieldType = field.getType();
				} catch (Exception e2) {}
				ValueDelegate vd = null;
				try {
					vd = ValueDelegate.eval(ioc, obj.getName(), fieldType, fld.getVal());
					singleton &= !vd.isDynamic();
					Object v = vd.get();
					Class<?> paramType = null == v ? null : v.getClass();
					Method setter = mirror.getSetter(fld.getName(), paramType);
					injectors.add(new Injector.SetterInjector(setter, vd));
				} catch (FailToMakeObjectException e) {
					throw e;
				} catch (ObjectNotFoundException e) {
					throw e;
				} catch (Exception e) {
					try {
						Field field = mirror.getField(fld.getName());
						injectors.add(new Injector.FieldInjector(field, vd));
					} catch (Exception e1) {
						throw new FailToMakeObjectException(String.format(
								"Dont know how to inject [%s].%s by value [%s] because '%s'",
								mirror.getType().getName(), fld.getName(), fld.getVal(), e1
										.getMessage()));
					}
				}
			}
		}
	}

	/*-----------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	ObjectCreator(NutIoc ioc, Class<T> classOfT, Obj obj) {
		this.ioc = ioc;
		this.obj = obj;
		try {
			Class<T> type = classOfT;
			if (obj.getType() != null)
				type = (Class<T>) Class.forName(obj.getType());
			mirror = ioc.mirrors().getMirror(type, obj.getName());
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
		events = evalLifecycle(mirror.getType(), obj);
		singleton = obj.isSingleton();
		evalBorning();
		evalFieldInjectors();
	}

	/*-----------------------------------------------------------------------------*/
	private NutIoc ioc;
	private Obj obj;
	private Mirror<T> mirror;
	private List<Injector> injectors;
	private Borning<T> borning;
	private Events<T> events;
	private boolean singleton;
	private ClassAgent ca;

	/*-----------------------------------------------------------------------------*/
	boolean isSingleton() {
		return singleton;
	}

	/*-----------------------------------------------------------------------------*/
	ObjectHolder<T> make() {
		T obj = borning.born();
		if (null != injectors)
			for (Injector inj : injectors)
				inj.inject(obj);
		return new ObjectHolder<T>(obj, events);
	}

}
