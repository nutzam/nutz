package org.nutz.aop.javassist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.nutz.aop.AbstractClassAgent;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Lang;

public class JavassistClassAgent extends AbstractClassAgent {

	private static ClassPool pool = new ClassPool(true);

	static {
		pool.appendClassPath(new LoaderClassPath(JavassistClassAgent.class.getClassLoader()));
	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> generate(Pair2 [] pair2s,String newName,Class<T> klass,Constructor<T> [] constructors){
		CtClass newClass = null;
		Class<T> nc = null;
		try {
			newClass = pool.get(newName);
		} catch (NotFoundException e1) {
			newClass = pool.makeClass(newName);
			CtClass oldClass = null;
			try {
				oldClass = pool.get(klass.getName());
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
			for (int i = 0; i < pair2s.length; i++) {
				Method method = pair2s[i].method;
				try {
					Javassist.addStaticField(pool, newClass, MethodInterceptor.class, "__lst_" + i);
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
			for (int i = 0; i < pair2s.length; i++) {
				Field field = thisClass.getDeclaredField("__lst_" + i);
				MethodInterceptor ml = new JavassistMethodInterceptor(pair2s[i].listeners);
				field.setAccessible(true);
				field.set(null, ml);
				field.setAccessible(true);
				field = thisClass.getDeclaredField("__m_" + i);
				field.set(null, pair2s[i].method);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return nc;
	}

}
