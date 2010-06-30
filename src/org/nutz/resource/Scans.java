package org.nutz.resource;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.nutz.lang.Lang;
import org.nutz.resource.impl.LocalResourceScan;
import org.nutz.resource.impl.WebResourceScan;

/**
 * 资源扫描的帮助函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Scans {

	private static final String FLT_CLASS = "^.+[.]class$";

	private static final ResourceScan local = new LocalResourceScan();

	/**
	 * 在磁盘目录或者 CLASSPATH 中搜索资源
	 * 
	 * @param src
	 *            起始路径
	 * @param regex
	 *            资源名需要匹配的正则表达式
	 * @return 资源列表
	 */
	public static List<NutResource> inLocal(String src, String regex) {
		return local.list(src, regex);
	}

	/**
	 * 搜索并返回给定包下所有的类（递归）
	 * 
	 * @param pkg
	 *            包名或者包路径
	 * @return
	 */
	public static List<Class<?>> inLocalPackage(String pkg) {
		return inLocalPackage(pkg, FLT_CLASS);
	}

	/**
	 * 搜索给定包下所有的类（递归），并返回所有符合正则式描述的类
	 * 
	 * @param pkg
	 *            包名或者包路径
	 * @param regex
	 *            正则表达式，请注意你需要匹配的名称为 'xxxx.class' 而不仅仅是类名，从而保证选出的对象都是类文件
	 * @return
	 */
	public static List<Class<?>> inLocalPackage(String pkg, String regex) {
		String packagePath = pkg.replace('.', '/').replace('\\', '/');
		return rs2class(packagePath, local.list(packagePath, regex));
	}

	/**
	 * 在 CLASSPATH 中搜索一个类的同一个包下以及下属包下所有子类
	 * 
	 * @param type
	 *            类对象
	 * @return 资源列表
	 */
	public static List<Class<?>> inLocal(Class<?> type) {
		return inLocal(type, FLT_CLASS);
	}

	/**
	 * 在 CLASSPATH 中搜索一个类的同一个包下以及下属包下所有名称符合给定正则表达式的子类
	 * 
	 * @param type
	 *            类对象
	 * @param regex
	 *            正则表达式，请注意你需要匹配的名称为 'xxxx.class' 而不仅仅是类名，从而保证选出的对象都是类文件
	 * @return 资源列表
	 */
	public static List<Class<?>> inLocal(Class<?> type, String regex) {
		String packagePath = type.getPackage().getName().replace('.', '/');
		String classPath = packagePath + "/" + type.getSimpleName() + ".class";
		return rs2class(packagePath, local.list(classPath, regex));
	}

	/**
	 * 将一组 NutResource 转换成 class 对象
	 * 
	 * @param packagePath
	 *            包前缀
	 * @param list
	 *            列表
	 * @return 类对象列表
	 */
	private static List<Class<?>> rs2class(String packagePath, List<NutResource> list) {
		List<Class<?>> re = new ArrayList<Class<?>>(list.size());
		if (!list.isEmpty()) {
			String firstItemName = list.get(0).getName().replace('\\', '/');
			int pos = firstItemName.lastIndexOf(packagePath);
			if (pos < 0)
				pos = 0;
			for (NutResource nr : list) {
				int r = nr.getName().lastIndexOf(".class");
				try {
					String className = nr	.getName()
											.substring(pos, r)
											.replace('/', '.')
											.replace('\\', '.');
					Class<?> klass = Class.forName(className);
					re.add(klass);
				}
				catch (ClassNotFoundException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
		return re;
	}

	/**
	 * 根据 ServletContext 创建一个 Web 应用的资源搜索器
	 * 
	 * @param context
	 *            ServletContext 对象
	 * @return Web 应用资源搜索器
	 * 
	 * @see org.nutz.resource.impl.WebResourceScan
	 */
	public static ResourceScan web(ServletContext context) {
		return new WebResourceScan(context);
	}
}
