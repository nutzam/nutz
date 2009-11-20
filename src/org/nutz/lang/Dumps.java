package org.nutz.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;

/**
 * 显示对象的信息，为日志以及调试提供帮助的函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class Dumps {

	/**
	 * 显示 Matcher 的详细信息
	 * 
	 * @param m
	 *            Matcher 对象
	 * @return 信息
	 */
	public static String matcher(Matcher m) {
		StringBuilder sb = new StringBuilder();
		if (m.find())
			for (int i = 0; i <= m.groupCount(); i++)
				sb.append(String.format("%2d: %s\n", i, m.group(i)));
		else
			sb.append(String.format("No found!"));
		return sb.toString();
	}

	/**
	 * 显示一个对象所有个 getter 函数返回，以及 public 的 Field 的值
	 * 
	 * @param obj
	 *            对象
	 * @return 信息
	 */
	public static String obj(Object obj) {
		if (null == obj)
			return "null";
		StringBuilder sb = new StringBuilder();
		sb.append(obj.getClass().getName());
		Mirror<?> mirror = Mirror.me(obj.getClass());

		sb.append("\n\n[Fields:]");
		for (Field f : mirror.getType().getFields())
			if (Modifier.isPublic(f.getModifiers()))
				try {
					sb.append(String.format("\n\t%10s : %s", f.getName(), f.get(obj)));
				} catch (Exception e1) {
					sb.append(String.format("\n\t%10s : %s", f.getName(), e1.getMessage()));
				}
		sb.append("\n\n[Methods:]");
		for (Method m : mirror.getType().getMethods())
			if (Modifier.isPublic(m.getModifiers()))
				if (m.getName().startsWith("get"))
					if (m.getParameterTypes().length == 0)
						try {
							sb.append(String.format("\n\t%10s : %s", m.getName(), m.invoke(obj)));
						} catch (Exception e) {
							sb.append(String.format("\n\t%10s : %s", m.getName(), e.getMessage()));
						}
		return sb.toString();
	}

}
