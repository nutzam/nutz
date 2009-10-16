package org.nutz.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.nutz.lang.Mirror.MatchType;
import org.nutz.lang.born.*;

@SuppressWarnings("unchecked")
public class MirrorBorning<T> implements Borning<T> {

	private Mirror<T> mirror;
	private Class<T> type;
	private Object[] args;
	private Object dynaArg;
	private BorningInvoker<T> invoker;

	public MirrorBorning(Mirror<T> mirror, Object... args) {
		this.mirror = mirror;
		this.type = mirror.getType();
		this.args = null == args ? new Object[0] : args;
		dynaArg = Mirror.evalArgToRealArray(args);
		if (this.args.length == 0) {
			evalNullArgs();
		} else {
			evalWithArgs();
		}
		if (null == invoker) {
			throw new BorningException(new RuntimeException("Don't know how to born it!"), type,
					args);
		}
	}

	public T born() {
		return born(args);
	}

	public T born(Object[] args) {
		try {
			return invoker.born(args);
		} catch (Throwable e) {
			throw new BorningException(e, type, args);
		}
	}

	public BorningInvoker<T> getInvoker() {
		return invoker;
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
					args[0] = Mirror.blankArrayArg(pts);
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
					args[0] = Mirror.blankArrayArg(pts);
					invoker = new MethodInvoker(m, args);
					return;
				}
			}
		}
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
		Class<?>[] argTypes = Mirror.evalToTypes(args);
		for (Constructor<?> cc : type.getConstructors()) {
			Class<?>[] pts = cc.getParameterTypes();
			MatchType mt = Mirror.matchParamTypes(pts, argTypes);
			if (MatchType.YES == mt) {
				invoker = new ConstructorInvoker(cc, args);
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, Mirror.blankArrayArg(pts));
				invoker = new ConstructorInvoker(cc, args);
				return;
			} else if (null != dynaArg && pts.length == 1 && pts[0] == dynaArg.getClass()) {
				invoker = new DynamicConstructorInvoker(cc, dynaArg);
				return;
			}
		}
		Method[] sms = mirror.getStaticMethods();
		for (Method m : sms) {
			Class<?>[] pts = m.getParameterTypes();
			MatchType mt = Mirror.matchParamTypes(pts, args);
			if (MatchType.YES == mt) {
				invoker = new MethodInvoker(m, args);
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, Mirror.blankArrayArg(pts));
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
		if (null == invoker)
			try {
				for (Constructor<?> cc : type.getConstructors()) {
					Class<?>[] pts = cc.getParameterTypes();
					if (pts.length == args.length) {
						args = Lang.array2ObjectArray(args, pts);
						invoker = new ConstructorInvoker(cc, args);
						return;
					}
				}
			} catch (Exception e) {}
		// casting static methods
		if (null == invoker)
			try {
				for (Method m : sms) {
					Class<?>[] pts = m.getParameterTypes();
					if (pts.length == args.length) {
						args = Lang.array2ObjectArray(args, pts);
						invoker = new MethodInvoker(m, args);
						return;
					}
				}
			} catch (Exception e) {}
	}

}
