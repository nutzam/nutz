package org.nutz.java;

import org.nutz.java.JavaLanguageObject;
import org.nutz.lang.Strings;

public class JavaPackage extends JavaLanguageObject implements Comparable<JavaLanguageObject> {

	private String name;

	public JavaPackage(String name) {
		super();
		this.name = name;
	}

	public int match(JavaPackage jp) {
		String[] mine = getPackages();
		String[] its = jp.getPackages();
		int i = 0;
		for (; i < Math.min(mine.length, its.length); i++)
			if (!mine[i].equals(its[i]))
				return i;
		return i;
	}

	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		if (obj instanceof JavaPackage)
			return name.equals(((JavaPackage) obj).name);
		return name.equals(obj.toString());
	}

	public String[] getPackages() {
		return Strings.splitIgnoreBlank(name, "[.]");
	}

	protected String renderSource() {
		return name;
	}

	public int compareTo(JavaLanguageObject o) {
		if (o instanceof JavaPackage)
			return name.compareTo(((JavaPackage) o).name);
		throw new RuntimeException("Can not compare with " + o.toString());
	}

}
