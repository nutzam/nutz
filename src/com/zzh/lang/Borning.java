package com.zzh.lang;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.zzh.castor.Castors;
import com.zzh.castor.FailToCastObjectException;
import com.zzh.lang.Mirror.MatchType;

@SuppressWarnings("unchecked")
public class Borning<T> {

	// --------------------------------------------------------------------
	static interface Invoker<T> {
		T invoke() throws Exception;
	}

	static class DynaConstructorInvoker<T> implements Invoker<T> {

		private Constructor<T> c;
		private Object arg;

		public DynaConstructorInvoker(Constructor<T> c, Object arg) {
			this.c = c;
			this.arg = arg;
		}

		public T invoke() throws Exception {
			return c.newInstance(arg);
		}
	}

	static class EmptyArgsConstructorInvoker<T> implements Invoker<T> {

		private Constructor<T> c;

		public EmptyArgsConstructorInvoker(Constructor<T> c) {
			this.c = c;
		}

		public T invoke() throws Exception {
			return c.newInstance();
		}
	}

	static class ConstructorInvoker<T> implements Invoker<T> {

		private Constructor<T> c;
		private Object[] args;

		public ConstructorInvoker(Constructor<T> c, Object[] args) {
			this.c = c;
			this.args = args;
		}

		public T invoke() throws Exception {
			return c.newInstance(args);
		}
	}

	static class EmptyArgsMethodInvoker<T> implements Invoker<T> {

		private Method method;

		public EmptyArgsMethodInvoker(Method method) {
			this.method = method;
		}

		public T invoke() throws Exception {
			return (T) method.invoke(null);
		}
	}

	static class DynaMethodInvoker<T> implements Invoker<T> {

		private Method method;
		private Object arg;

		public DynaMethodInvoker(Method method, Object arg) {
			this.method = method;
			this.arg = arg;
		}

		public T invoke() throws Exception {
			return (T) method.invoke(null, arg);
		}
	}

	static class MethodInvoker<T> implements Invoker<T> {

		private Method method;
		private Object[] args;

		public MethodInvoker(Method method, Object[] args) {
			this.method = method;
			this.args = args;
		}

		public T invoke() throws Exception {
			return (T) method.invoke(null, args);
		}
	}

	// --------------------------------------------------------------------
	private Mirror<T> mirror;
	private Class<T> type;
	private Object[] args;
	private Object dynaArg;
	private Invoker<T> invoker;

	public Borning(Mirror<T> mirror, Object... args) {
		this.mirror = mirror;
		this.type = mirror.getType();
		this.args = null == args ? new Object[0] : args;
		if (this.args.length > 0) {
			Class<?> type = this.args[0].getClass();
			for (int i = 1; i < this.args.length; i++) {
				if (this.args[i].getClass() != type) {
					type = null;
					break;
				}
			}
			if (type != null) {
				dynaArg = Array.newInstance(type, this.args.length);
				for (int i = 0; i < this.args.length; i++) {
					Array.set(dynaArg, i, this.args[i]);
				}
			}
		}
		eval();
	}

	public T born() {
		try {
			return invoker.invoke();
		} catch (Exception e) {
			throw new BorningException(e, type, args);
		}
	}

	private void eval() {
		if (args.length == 0)
			evalNullArgs();
		else
			evalWithArgs();
		if (null == invoker)
			throw Lang.makeThrow(BorningException.class, "Don't know how to borning [%s]", type
					.getName());
	}

	/**
	 * It will find:
	 * <ol>
	 * <li>Default Constructor
	 * <li>Static Method without args
	 * <li>Constructor with one array arguments
	 * <li>Static Method with one array arguments
	 * </ol>
	 */
	private void evalNullArgs() {
		try {
			invoker = new EmptyArgsConstructorInvoker(type.getConstructor());
		} catch (Exception e) {
			Method[] sms = mirror.getStaticMethods();
			for (Method m : sms) {
				if (m.getReturnType() == type && m.getParameterTypes().length == 0) {
					invoker = new EmptyArgsMethodInvoker(m);
					return;
				}
			}
			// constructor with array input
			for (Constructor<?> cc : type.getConstructors()) {
				Class<?>[] pts = cc.getParameterTypes();
				if (pts.length == 1 && pts[0].isArray()) {
					args = new Object[1];
					args[0] = blankArrayArg(pts);
					invoker = new ConstructorInvoker(cc, args);
					return;
				}
			}
			// static
			for (Method m : sms) {
				Class<?>[] pts = m.getParameterTypes();
				if (m.getReturnType() == type && m.getParameterTypes().length == 1
						&& pts[0].isArray()) {
					args = new Object[1];
					args[0] = blankArrayArg(pts);
					invoker = new MethodInvoker(m, args);
					return;
				}
			}
		}
	}

	private Object[] blankArrayArg(Class<?>[] pts) {
		return (Object[]) Array.newInstance(pts[pts.length - 1].getComponentType(), 0);
	}

	/**
	 * It will find:
	 * <ol>
	 * <li>Constructor with same args
	 * <li>Constractor with args and the last one is array arguments
	 * <li>Static Method with same args
	 * <li>Static Method with args and the last one is array arguments
	 * <li>Try to find one constrcture with same number of args, and cast all
	 * args value
	 * <li>Try to find one static method with same number of args, and cast all
	 * args value
	 * </ol>
	 */

	private void evalWithArgs() {
		for (Constructor<?> cc : type.getConstructors()) {
			Class<?>[] pts = cc.getParameterTypes();
			MatchType mt = Mirror.matchMethodParamsType(pts, args);
			if (MatchType.YES == mt) {
				invoker = new ConstructorInvoker(cc, args);
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, blankArrayArg(pts));
				invoker = new ConstructorInvoker(cc, args);
				return;
			} else if (null != dynaArg && pts.length == 1) {
				if (pts[0] == dynaArg.getClass()) {
					invoker = new DynaConstructorInvoker(cc, dynaArg);
					return;
				}
			}
		}
		Method[] sms = mirror.getStaticMethods();
		for (Method m : sms) {
			Class<?>[] pts = m.getParameterTypes();
			MatchType mt = Mirror.matchMethodParamsType(pts, args);
			if (MatchType.YES == mt) {
				invoker = new MethodInvoker(m, args);
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, blankArrayArg(pts));
				invoker = new MethodInvoker(m, args);
				return;
			} else if (null != dynaArg && pts.length == 1) {
				if (pts[0] == dynaArg.getClass()) {
					invoker = new DynaMethodInvoker(m, dynaArg);
					return;
				}
			}
		}
		// casting constructor
		try {
			for (Constructor<?> cc : type.getConstructors()) {
				Class<?>[] pts = cc.getParameterTypes();
				if (pts.length == args.length) {
					args = castArguments(pts, args);
					invoker = new ConstructorInvoker(cc, args);
					return;
				}
			}
		} catch (Exception e) {}
		// casting static methods
		try {
			for (Method m : sms) {
				Class<?>[] pts = m.getParameterTypes();
				if (pts.length == args.length) {
					args = castArguments(pts, args);
					invoker = new MethodInvoker(m, args);
					return;
				}
			}
		} catch (Exception e) {}
	}

	private Object[] castArguments(Class<?>[] pts, Object[] args) throws FailToCastObjectException {
		Object[] newArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			newArgs[i] = Castors.me().castTo(args[i], pts[i]);
		}
		return newArgs;
	}

}
