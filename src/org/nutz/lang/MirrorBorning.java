package org.nutz.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.nutz.lang.MatchType;
import org.nutz.lang.born.*;

@SuppressWarnings("unchecked")
class MirrorBorning<T> {

	private Mirror<T> mirror;
	private Class<T> type;
	private Object dynaArg;
	private Borning<T> borning;
	private Object[] realArgs;

	public MirrorBorning(Mirror<T> mirror, Object... args) {
		this.mirror = mirror;
		this.type = mirror.getType();
		if (null == args)
			args = new Object[0];
		dynaArg = Mirror.evalArgToRealArray(args);
		if (args.length == 0) {
			evalNullArgs(args);
		} else {
			evalWithArgs(args);
		}
		if (null == borning) {
			throw new BorningException(new RuntimeException("Don't know how to born it!"), type,
					args);
		}
	}

	public T born() {
		try {
			return borning.born(realArgs);
		} catch (Throwable e) {
			throw new BorningException(e, type, realArgs);
		}
	}

	public Borning<T> getBorning() {
		return borning;
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
	private void evalNullArgs(Object[] args) {
		try {
			borning = new EmptyArgsConstructorBorning(type.getConstructor());
			realArgs = new Object[0];
		} catch (Exception e) {
			Method[] sms = mirror.getStaticMethods();
			for (Method m : sms) {
				if (m.getReturnType() == type && m.getParameterTypes().length == 0) {
					borning = new EmptyArgsMethodBorning(m);
					realArgs = new Object[0];
					return;
				}
			}
			// constructor with array input
			for (Constructor<?> cc : type.getConstructors()) {
				Class<?>[] pts = cc.getParameterTypes();
				if (pts.length == 1 && pts[0].isArray()) {
					args = new Object[1];
					args[0] = Mirror.blankArrayArg(pts);
					borning = new ConstructorBorning(cc);
					realArgs = args;
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
					borning = new MethodBorning(m);
					realArgs = args;
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

	private void evalWithArgs(Object[] args) {
		Class<?>[] argTypes = Mirror.evalToTypes(args);
		for (Constructor<?> cc : type.getConstructors()) {
			Class<?>[] pts = cc.getParameterTypes();
			MatchType mt = Mirror.matchParamTypes(pts, argTypes);
			if (MatchType.YES == mt) {
				borning = new ConstructorBorning(cc);
				realArgs = args;
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, Mirror.blankArrayArg(pts));
				borning = new ConstructorBorning(cc);
				realArgs = args;
				return;
			} else if (null != dynaArg && pts.length == 1 && pts[0] == dynaArg.getClass()) {
				borning = new DynamicConstructorBorning(cc);
				realArgs = args;
				return;
			}
		}
		Method[] sms = mirror.getStaticMethods();
		for (Method m : sms) {
			Class<?>[] pts = m.getParameterTypes();
			MatchType mt = Mirror.matchParamTypes(pts, args);
			if (MatchType.YES == mt) {
				borning = new MethodBorning(m);
				realArgs = args;
				return;
			} else if (MatchType.LACK == mt) {
				args = Lang.arrayLast(args, Mirror.blankArrayArg(pts));
				borning = new MethodBorning(m);
				realArgs = args;
				return;
			} else if (null != dynaArg && pts.length == 1) {
				if (pts[0] == dynaArg.getClass()) {
					borning = new DynaMethodBorning(m);
					realArgs = args;
					return;
				}
			}
		}
		// casting constructor
		if (null == borning)
			try {
				for (Constructor<?> cc : type.getConstructors()) {
					Class<?>[] pts = cc.getParameterTypes();
					if (pts.length == args.length) {
						args = Lang.array2ObjectArray(args, pts);
						borning = new ConstructorBorning(cc);
						realArgs = args;
						return;
					}
				}
			} catch (Exception e) {}
		// casting static methods
		if (null == borning)
			try {
				for (Method m : sms) {
					Class<?>[] pts = m.getParameterTypes();
					if (pts.length == args.length) {
						args = Lang.array2ObjectArray(args, pts);
						borning = new MethodBorning(m);
						realArgs = args;
						return;
					}
				}
			} catch (Exception e) {}
	}

}
