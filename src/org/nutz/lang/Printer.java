package org.nutz.lang;

import static java.lang.System.out;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;

public abstract class Printer {

	public static void dump(Matcher m) {
		if (m.find())
			for (int i = 0; i <= m.groupCount(); i++)
				out.printf("%2d: %s\n", i, m.group(i));
		else
			out.println("No found!");
	}

	public static String dumpObject(Object obj) {
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
