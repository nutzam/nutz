package org.nutz.lang;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Mirror.MatchType;

import static java.lang.String.*;

public class Invoking {
	/*------------------------------------------------------------------------*/
	private static abstract class Invoker {

		protected Method method;

		public Invoker(Method method) {
			this.method = method;
		}

		abstract Object invoke(Object obj) throws Exception;
	}

	/*------------------------------------------------------------------------*/
	private static class DefaultInvoker extends Invoker {

		private Object[] args;

		public DefaultInvoker(Method method, Object[] args) {
			super(method);
			this.args = args;
		}

		@Override
		Object invoke(Object obj) throws Exception {
			return method.invoke(obj, args);
		}
	}

	/*------------------------------------------------------------------------*/
	private static class DynamicArgsInvoker extends Invoker {

		private Object args;

		public DynamicArgsInvoker(Method method, Object args) {
			super(method);
			this.args = args;
		}

		@Override
		Object invoke(Object obj) throws Exception {
			return method.invoke(obj, args);
		}
	}

	/*------------------------------------------------------------------------*/
	private static class NullArgInvoker extends Invoker {

		public NullArgInvoker(Method method) {
			super(method);
		}

		@Override
		Object invoke(Object obj) throws Exception {
			return method.invoke(obj);
		}

	}

	/*------------------------------------------------------------------------*/

	public Invoking(Class<?> type, String methodName, Object... args) {
		try {
			// get directoy
			if (null == args || args.length == 0) {
				invoker = new NullArgInvoker(type.getMethod(methodName));
			} else {
				// get all same name methods
				Method[] all = type.getMethods();
				List<Method> candidates = new ArrayList<Method>(all.length);
				for (Method m : all)
					if (m.getName().equals(methodName)) {
						// int mod =
						// m.getParameterTypes().length -
						// args.length;
						// if (mod == 0 || mod == 1)
						candidates.add(m);
					}
				// get argTypes
				Class<?>[] argTypes = Mirror.evalToTypes(args);
				Object dynaArg = Mirror.evalArgToRealArray(args);
				// check the candidate methods can be
				// match or not
				for (Iterator<Method> it = candidates.iterator(); it.hasNext();) {
					Method m = it.next();
					Class<?>[] pts = m.getParameterTypes();
					MatchType mr = Mirror.matchParamTypes(pts, argTypes);
					if (MatchType.YES == mr) {
						invoker = new DefaultInvoker(m, args);
						break;
					} else if (MatchType.LACK == mr) {
						invoker = new DefaultInvoker(m, Lang.arrayLast(args, Mirror
								.blankArrayArg(pts)));
						break;
					} else if (null != dynaArg && pts.length == 1 && pts[0] == dynaArg.getClass()) {
						invoker = new DynamicArgsInvoker(m, dynaArg);
						break;
					}
				}
				// if fail to match, try to cast args
				// to same length param method
				// ro to last param is "T...", length+1
				// method
				if (null == invoker)
					try {
						for (Iterator<Method> it = candidates.iterator(); it.hasNext();) {
							Method m = it.next();
							Class<?>[] pts = m.getParameterTypes();
							if (pts.length == args.length) {
								invoker = new DefaultInvoker(m, Lang.array2ObjectArray(args, pts));
							} else if (pts.length == args.length + 1 && pts[args.length].isArray()) {
								invoker = new DefaultInvoker(m, Lang.array2ObjectArray(args, pts));
							}
						}
					} catch (Exception e) {}
				// to same length + last is dynamic
				// argument method
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		if (null == invoker)
			throw new InvokingException("Don't know how to invoke [%s].%s() by args:\n %s", type
					.getName(), methodName, Lang.concatBy('\n', args));
		msg = format("Fail to invoke [%s].%s() by args:\n %s", type.getName(), methodName, Lang
				.concatBy('\n', args))
				+ "\nFor the reason: %s";
	}

	private String msg;
	private Invoker invoker;

	public Object invoke(Object obj) {
		try {
			return invoker.invoke(obj);
		} catch (Exception e) {
			throw new InvokingException(msg, e.getMessage());
		}
	}

}
