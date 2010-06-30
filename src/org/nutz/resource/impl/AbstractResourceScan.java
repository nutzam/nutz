package org.nutz.resource.impl;

import org.nutz.plugin.Plugin;
import org.nutz.resource.ResourceScan;

public abstract class AbstractResourceScan implements ResourceScan, Plugin {

	// TODO 删除下面的注释
	/*
	zzh: 下面的两个函数不再有用了
	
	protected String getClassPath() {
		Properties properties = System.getProperties();
		if (properties != null)
			for (Entry<Object, Object> entry : properties.entrySet())
				if (entry.getValue() != null
					&& "java.class.path".equalsIgnoreCase(entry.getKey().toString()))
					return entry.getValue().toString();
		Map<String, String> env = System.getenv();
		if (env != null)
			for (Entry<String, String> entry : env.entrySet())
				if ("CLASSPATH".equalsIgnoreCase(entry.getKey()))
					return entry.getValue();
		return null;
	}

	protected String[] splitedClassPath() {
		String CLASSPATH = getClassPath();
		if (CLASSPATH != null) {
			Object pathSeparator = System.getProperties().get("path.separator");
			if (pathSeparator == null) {
				if (Lang.isWin())
					pathSeparator = ";";
				else
					pathSeparator = ":";
			}
			return CLASSPATH.split(pathSeparator.toString());
		}
		return null;
	}
	*/
}
