package org.nutz.aop.javassist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

import static java.lang.String.*;

public class Javassist {

	public static CtClass[] makeParams(ClassPool pool, Class<?>[] pts) {
		try {
			CtClass[] parameters = new CtClass[pts.length];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = pool.get(pts[i].getName());
			}
			return parameters;
		} catch (NotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static String getCallSuper(Method method) {
		StringBuilder sb = new StringBuilder();
		int n = method.getParameterTypes().length;
		if (n > 0) {
			sb.append("$1");
			for (int i = 1; i < n; i++)
				sb.append(",$").append(i + 1);
		}
		Class<?> rt = method.getReturnType();
		if (rt != void.class && rt.isPrimitive()) {
			Mirror<?> mirror = Mirror.me(rt);
			return format("%s.valueOf(super.%s(%s));", mirror.getWrapperClass().getName(), method
					.getName(), sb.toString());
		}

		return format("super.%s(%s);", method.getName(), sb.toString());
	}

	public static String getCallSuper(Constructor<?> c) {
		StringBuilder sb = new StringBuilder("super(");
		int n = c.getParameterTypes().length;
		if (n > 0) {
			sb.append("$1");
			for (int i = 1; i < n; i++)
				sb.append(",$").append(i + 1);
		}
		return sb.append(");").toString();
	}

	public static Object[] array(Object... eles) {
		return eles;
	}

	public static String getParamsAsObjectArray(int n) {
		StringBuilder sb = new StringBuilder(",org.nutz.aop.javassist.Javassist.array(");
		if (n > 0) {
			sb.append("$1");
			for (int i = 1; i < n; i++)
				sb.append(",$").append(i + 1);
		}
		return sb.append(")").toString();
	}

	public static void addStaticField(ClassPool pool, CtClass newClass, Class<?> fieldType,
			String name) throws CannotCompileException, NotFoundException {
		CtField cf = new CtField(pool.get(fieldType.getName()), name, newClass);
		cf.setModifiers(javassist.Modifier.STATIC | javassist.Modifier.PUBLIC);
		newClass.addField(cf, "null");
	}

	public static CtMethod makeOverrideMethod(ClassPool pool, CtClass newClass, Method method,
			int num) throws NotFoundException, CannotCompileException {
		CtMethod cm = new CtMethod(pool.get(method.getReturnType().getName()), method.getName(),
				makeParams(pool, method.getParameterTypes()), newClass);
		String body = makeOverrideMethodBody(method, num);
		// System.out.println(Strings.dup('-', 60));
		// System.out.printf("%s%s\n", method.getName(),
		// Javassist.getMethodDescriptor(method));
		// System.out.println(body);
		cm.setBody(body);
		return cm;
	}

	private static String makeOverrideMethodBody(Method method, int num) {
		if (void.class == method.getReturnType())
			return makeOverrideVoidMethodBody(method, num);
		return makeOverrideReturnMethodBody(method, num);
	}

	private static String makeOverrideReturnMethodBody(Method method, int num) {
		Class<?> rt = method.getReturnType();
		String body = "{";
		body += prepareParamsArray(method);
		body += "\ntry {";
		body += "\n	Object re = null;";
		body += "\n	if (__lst_" + num + ".beforeInvoke((Object) this, __m_" + num + ", params))";
		body += "\n		re = " + getCallSuper(method);
		body += "\n	re = __lst_" + num + ".afterInvoke(this, re, __m_" + num + ", params);";
		if (rt.isPrimitive()) {
			Mirror<?> mirror = Mirror.me(rt);
			body += "\n	return "
					+ format("((%s)re).%sValue();", mirror.getWrapper().getName(), mirror.getType()
							.getName());
		} else if (rt.isArray()) {
			body += "\n	return (" + rt.getComponentType().getName() + "[]) re;";
		} else
			body += "\n	return (" + rt.getName() + ") re;";
		body += "\n} catch (Exception e) {";
		body += "\n	__lst_" + num + ".whenException(e, this, __m_" + num + ", params);";
		body += "\n	throw org.nutz.lang.Lang.wrapThrow(e);";
		body += "\n} catch (Throwable e) {";
		body += "\n	__lst_" + num + ".whenError(e, this, __m_" + num + ", params);";
		body += "\n	throw org.nutz.lang.Lang.wrapThrow(e);";
		body += "\n}";
		body += "\n}";
		return body;
	}

	private static String makeOverrideVoidMethodBody(Method method, int num) {
		String body = "{";
		body += prepareParamsArray(method);
		body += "\ntry {";
		body += "\n	if (__lst_" + num + ".beforeInvoke(this, __m_" + num + ", params))";
		body += "\n		" + getCallSuper(method);
		body += "\n	__lst_" + num + ".afterInvoke(this, null, __m_" + num + ", params);";
		body += "\n} catch (Exception e) {";
		body += "\n	__lst_" + num + ".whenException(e, this, __m_" + num + ", params);";
		body += "\n	throw org.nutz.lang.Lang.wrapThrow(e);";
		body += "\n} catch (Throwable e) {";
		body += "\n	__lst_" + num + ".whenError(e, this, __m_" + num + ", params);";
		body += "\n	throw org.nutz.lang.Lang.wrapThrow(e);";
		body += "\n}";
		body += "\n}";
		return body;
	}

	private static String prepareParamsArray(Method method) {
		Class<?>[] pts = method.getParameterTypes();
		String body = "\nObject[] params = new Object[" + pts.length + "];";
		for (int i = 0; i < pts.length; i++) {
			if (pts[i].isPrimitive()) {
				Mirror<?> mirror = Mirror.me(pts[i]);
				body += "\nparams[" + i + "] = " + mirror.getWrapper().getName() + ".valueOf($"
						+ (i + 1) + ");";
			} else
				body += "\nparams[" + i + "] = $" + (i + 1) + ";";
		}
		return body;
	}
}
