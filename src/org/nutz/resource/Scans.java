package org.nutz.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.impl.FileResource;
import org.nutz.resource.impl.LocalResourceScan;
import org.nutz.resource.impl.WebResourceScan;

/**
 * 资源扫描的帮助函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Scans {

	private static final Scans me = new Scans();
	
	private static final Log LOG = Logs.getLog(Scans.class);
	
	private Scans(){}
	
	public static final Scans me() {
		return me;
	}
	
	private static final String FLT_CLASS = "^.+[.]class$";

	private final ResourceScan local = new LocalResourceScan();
	
	private WebResourceScan web;
	
	public Scans init(ServletContext servletContext) {
		if (servletContext == null)
			web = null;
		else
			web = new WebResourceScan(servletContext);
		return this;
	}
	
	public ResourceScan getScaner() {
		if (web != null)
			return web;
		else
			return local;
	}

	/**
	 * 在磁盘目录或者 CLASSPATH(包括 jar) 中搜索资源
	 * <ul>
	 * <li>由于 jar 文件不包括目录，所以给出的 src 必须是个文件
	 * </ul>
	 * 
	 * @param src
	 *            起始路径
	 * @param regex
	 *            资源名需要匹配的正则表达式
	 * @return 资源列表
	 */
	public List<NutResource> scan(String src, String regex) {
		List<NutResource> list = isFile(src);
		if (list != null)
			return list;
		return getScaner().list(src, regex);
	}
	
	public List<NutResource> scan(String src) {
		List<NutResource> list = isFile(src);
		if (list != null)
			return list;
		return getScaner().list(src, null);
	}

	/**
	 * 搜索并返回给定包下所有的类（递归）
	 * 
	 * @param pkg
	 *            包名或者包路径
	 */
	public List<Class<?>> scanPackage(String pkg) {
		return scanPackage(pkg, FLT_CLASS);
	}

	/**
	 * 搜索给定包下所有的类（递归），并返回所有符合正则式描述的类
	 * 
	 * @param pkg
	 *            包名或者包路径
	 * @param regex
	 *            正则表达式，请注意你需要匹配的名称为 'xxxx.class' 而不仅仅是类名，从而保证选出的对象都是类文件
	 */
	public List<Class<?>> scanPackage(String pkg, String regex) {
		String packagePath = pkg.replace('.', '/').replace('\\', '/');
		return rs2class(packagePath, getScaner().list(packagePath, regex));
	}
	
	public List<Class<?>> scanPackage(Class<?> classZ, String regex) {
		return scanPackage(classZ.getPackage().getName(), regex);
	}
	
	public List<Class<?>> scanPackage(Class<?> classZ) {
		return scanPackage(classZ.getPackage().getName(), FLT_CLASS);
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
				if (r < 0) {
					LOG.infof("Resource can't map to Class, Resource %s",nr );
					continue;
				}
				try {
					String className = nr.getName()
											.substring(pos, r)
											.replace('/', '.')
											.replace('\\', '.');
					Class<?> klass = Class.forName(className);
					re.add(klass);
				}
				catch (ClassNotFoundException e) {
					LOG.infof("Resource can't map to Class, Resource %s",nr );
				}
			}
		}
		return re;
	}
	
	public List<NutResource> isFile(String src){
		File file = Files.findFile(src);
		if (file == null || file.isDirectory())
			return null;
		List<NutResource> list = new ArrayList<NutResource>(1);
		list.add(new FileResource(file.getParentFile().getAbsolutePath(), file));
		return list;
	}
}
