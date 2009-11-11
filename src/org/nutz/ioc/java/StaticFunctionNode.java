package org.nutz.ioc.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.nutz.ioc.IocMaking;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class StaticFunctionNode extends ChainNode {

	private Method method;
	private ChainNode[] args;

	public StaticFunctionNode(String className, String name, ChainNode[] args) {
		try {
			Mirror<?> mirror = Mirror.me((Class<?>) Class.forName(className));
			if (null == args || args.length == 0) {
				try {
					method = mirror.getGetter(name);
				} catch (NoSuchMethodException e) {
					throw Lang.makeThrow("Method '%s' don't find in '%s'", name, mirror);
				}
			} else {
				Method[] ms = mirror.findMethods(name, args.length);
				if (0 == ms.length)
					throw Lang.makeThrow("Method '%s' don't find in '%s'", name, mirror);
				this.args = args;
				this.method = ms[0];
				if (!Modifier.isStatic(method.getModifiers()))
					throw Lang.makeThrow("Method '%s' of '%s' must be static", name, mirror);
			}
		} catch (ClassNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	protected Object getValue(IocMaking ing, Object obj) throws Exception {
		if (null == args || args.length == 0) {
			return method.invoke(obj);
		}
		Object[] fas = new Object[args.length];
		for (int i = 0; i < args.length; i++)
			fas[i] = args[i].getValue(ing, null);
		return method.invoke(obj, fas);
	}

	protected String asString() {
		StringBuilder sb = new StringBuilder();
		if (null != args && args.length > 0) {
			sb.append(args[0].toString());
			for (int i = 1; i < args.length; i++)
				sb.append(", ").append(args[i].toString());
		}
		return String.format("%s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), sb);
	}

}
