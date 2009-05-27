package com.zzh.ioc.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.zzh.ioc.Deposer;
import com.zzh.ioc.impl.DynamicBorning;
import com.zzh.ioc.FailToMakeObjectException;
import com.zzh.ioc.Ioc;
import com.zzh.ioc.MethodDeposer;
import com.zzh.ioc.ObjectNotFoundException;
import com.zzh.ioc.meta.Fld;
import com.zzh.ioc.meta.Obj;
import com.zzh.ioc.meta.Val;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.born.Borning;

public class ObjectCreator<T> {

	@SuppressWarnings("unchecked")
	private static <T> Mirror<T> evalMirror(Class<T> classOfT, Obj obj) {
		if (!Strings.isBlank(obj.getType()))
			try {
				return (Mirror<T>) Mirror.me(Class.forName(obj.getType()));
			} catch (Exception e) {}
		if (null != classOfT)
			return Mirror.me(classOfT);
		throw Lang.makeThrow("Don't know object the class of [%s]!", obj.getName());
	}

	/*-----------------------------------------------------------------------------*/
	@SuppressWarnings("unchecked")
	private static <T> Deposer<T> evalDeposer(Class<T> classOfT, Obj obj) {
		if (!Strings.isBlank(obj.getDeposeby())) {
			try {
				Method m = classOfT.getMethod(obj.getDeposeby());
				return (Deposer<T>) new MethodDeposer(m);
			} catch (Exception e) {
				throw Lang.makeThrow(
						"In Object [%s]: Can not find depose method [%s()] in class [%s]", obj
								.getName(), obj.getDeposeby(), classOfT.getName());
			}
		}
		if (!Strings.isBlank(obj.getDeposer())) {
			try {
				Class<?> depType = Class.forName(obj.getDeposer());
				return (Deposer<T>) depType.newInstance();
			} catch (Exception e) {
				throw Lang.makeThrow("In Object [%s]: Can not create deposer [%s], because:\n%s",
						obj.getName(), obj.getDeposer(), e.getMessage());
			}
		}
		return null;
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
			borning = ((DynamicBorning<T>) borning).getBorning();
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
				ValueDelegate vd = ValueDelegate.eval(ioc, obj.getName(), fieldType, fld.getVal());
				singleton &= !vd.isDynamic();
				try {
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
								"Nut dont know how to inject [%s].%s by value [%s] because '%s'",
								mirror.getType().getName(), fld.getName(), fld.getVal(), e1
										.getMessage()));
					}
				}
			}
		}
	}

	/*-----------------------------------------------------------------------------*/
	ObjectCreator(Ioc ioc, Class<T> classOfT, Obj obj) {
		this.ioc = ioc;
		this.obj = obj;
		mirror = evalMirror(classOfT, obj);
		deposer = evalDeposer(mirror.getType(), obj);
		singleton = obj.isSingleton();
		evalBorning();
		evalFieldInjectors();
	}

	/*-----------------------------------------------------------------------------*/
	private Ioc ioc;
	private Obj obj;
	private Mirror<T> mirror;
	private List<Injector> injectors;
	private Borning<T> borning;
	private Deposer<T> deposer;
	private boolean singleton;

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
		return new ObjectHolder<T>(obj, deposer);
	}

}
