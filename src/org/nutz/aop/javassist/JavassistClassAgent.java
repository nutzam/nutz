package org.nutz.aop.javassist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.nutz.aop.ClassAgent;
import org.nutz.aop.MethodListener;
import org.nutz.aop.MethodMatcher;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class JavassistClassAgent extends ClassLoader implements ClassAgent {

	private static ClassPool pool = new ClassPool(true);

	static {
		pool.appendClassPath(new LoaderClassPath(JavassistClassAgent.class.getClassLoader()));
	}

	public JavassistClassAgent() {
		pairs = new ArrayList<Pair>();
	}

	private ArrayList<Pair> pairs;

	private static class Pair {
		Pair(MethodMatcher matcher, MethodListener listener) {
			this.matcher = matcher;
			this.listener = listener;
		}

		MethodMatcher matcher;
		MethodListener listener;
	}

	private static class Pair2 {
		Pair2(Method method, MethodListener listener) {
			this.method = method;
			this.listener = listener;
		}

		Method method;
		MethodListener listener;
	}

	public ClassAgent addListener(MethodMatcher matcher, MethodListener listener) {
		if (null != listener)
			pairs.add(new Pair(matcher, listener));
		return this;
	}

	private <T> Pair2[] findMatchedMethod(Class<T> klass) {
		Method[] all = Mirror.me(klass).getAllDeclaredMethodsWithoutTop();
		ArrayList<Pair2> mmls = new ArrayList<Pair2>(all.length);
		for (Method m : all) {
			int mod = m.getModifiers();
			if (mod == 0 || Modifier.isStatic(mod) || Modifier.isPrivate(mod))
				continue;
			ArrayList<MethodListener> mls = new ArrayList<MethodListener>();
			for (Pair p : pairs)
				if (p.matcher.match(m))
					mls.add(p.listener);
			if (mls.size() > 0) {
				mmls.add(new Pair2(m, new JavassistMethodListener(mls)));
			}
		}
		Pair2[] list = mmls.toArray(new Pair2[mmls.size()]);
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> define(Class<T> klass) {
		Pair2[] pairs = findMatchedMethod(klass);
		if (pairs.length == 0)
			return klass;
		AgentClass ac = new AgentClass(klass);
		try {
			return (Class<T>) Class.forName(ac.getNewName(), false, this);
		} catch (ClassNotFoundException e2) {
			try {
				return (Class<T>) Class.forName(ac.getNewName());
			} catch (ClassNotFoundException e1) {
				try {
					return (Class<T>) getClass().getClassLoader().loadClass(ac.getNewName());
				} catch (ClassNotFoundException e) {}
			}
		}
		CtClass newClass = null;
		Class<T> nc = null;
		try {
			newClass = pool.get(ac.getNewName());
		} catch (NotFoundException e1) {
			newClass = pool.makeClass(ac.getNewName());
			CtClass oldClass = null;
			try {
				oldClass = pool.get(ac.getOldName());
				newClass.setSuperclass(oldClass);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
			/*
			 * Add constructors
			 */
			for (Constructor<?> c : klass.getConstructors()) {
				CtClass[] params = Javassist.makeParams(pool, c.getParameterTypes());
				CtConstructor cc = new CtConstructor(params, newClass);
				try {
					cc.setBody(Javassist.getCallSuper(c));
					newClass.addConstructor(cc);
				} catch (CannotCompileException e) {
					throw Lang.wrapThrow(e);
				}
			}
			/*
			 * Add Methods
			 */
			for (int i = 0; i < pairs.length; i++) {
				Method method = pairs[i].method;
				try {
					Javassist.addStaticField(pool, newClass, MethodListener.class, "__lst_" + i);
					Javassist.addStaticField(pool, newClass, Method.class, "__m_" + i);
					CtMethod cm = Javassist.makeOverrideMethod(pool, newClass, method, i);
					newClass.addMethod(cm);
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		try {
			nc = (Class<T>) newClass.toClass();
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		// init static stub
		Class<?> thisClass = nc;
		try {
			for (int i = 0; i < pairs.length; i++) {
				Field field = thisClass.getDeclaredField("__lst_" + i);
				MethodListener ml = pairs[i].listener;
				field.setAccessible(true);
				field.set(null, ml);
				field.setAccessible(true);
				field = thisClass.getDeclaredField("__m_" + i);
				field.set(null, pairs[i].method);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return nc;
	}

}
