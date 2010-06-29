package org.nutz.resource;

import java.util.List;

import org.nutz.resource.impl.LocalResourceScan;

public class Scans {

	private static final ResourceScan local = new LocalResourceScan();

	/**
	 * 在磁盘目录或者 CLASSPATH 中搜索资源
	 * 
	 * @param src
	 *            起始路径
	 * @param filter
	 *            资源名需要匹配的正则表达式
	 * @return 资源列表
	 */
	public static List<NutResource> local(String src, String filter) {
		return local.list(src, filter);
	}

	/**
	 * 在 CLASSPATH 中搜索一个类，同一个包以及所有子包下的所有类
	 * 
	 * @param type
	 *            类对象
	 * @return 资源列表
	 */
	public static List<NutResource> local(Class<?> type) {
		return local.list(type.getName().replace('.', '/') + ".class", ".class");
	}

}
