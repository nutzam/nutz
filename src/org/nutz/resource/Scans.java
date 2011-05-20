package org.nutz.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

import org.nutz.castor.Castors;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.impl.FileResource;
import org.nutz.resource.impl.JarEntryResource;
import org.nutz.resource.impl.LocalResourceScan;
import org.nutz.resource.impl.WebResourceScan;

/**
 * 资源扫描的帮助函数集
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Scans {

	private static final Scans me = new Scans();

	private static final Log LOG = Logs.get();

	private Scans() {}

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
		ResourceScan scaner = web == null ? local : web;
		if (LOG.isDebugEnabled())
			LOG.debugf("Scan Resource by %s", scaner);
		return scaner;
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
		File file = Files.findFile(src);
		if (file != null) {
			if (file.isFile()) {
				src = src.replace('\\', '/');
				src = src.substring(0, src.lastIndexOf("/") + 1);
			} else if (isInJar(file)) {// 需要判断到底是Zip文件中的文件还是文件夹
				NutResource nutResource = makeJarNutResource(file);
				if (nutResource != null) {
					List<NutResource> list = new ArrayList<NutResource>(1);
					list.add(nutResource);
					return list;
				}
			}
		}
		List<NutResource> list = getScaner().list(src, regex);
		if (LOG.isDebugEnabled())
			LOG.debugf("Found %s resource by src( %s ) , regex( %s )", list.size(), src, regex);
		return list;
	}

	public List<NutResource> scan(String src) {
		return scan(src, null);
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
		if (!packagePath.endsWith("/"))
			packagePath += "/";
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
		if (packagePath.endsWith("/"))
			packagePath = packagePath.substring(0, packagePath.length() - 1);
		List<Class<?>> re = new ArrayList<Class<?>>(list.size());
		if (!list.isEmpty()) {
			for (NutResource nr : list) {
				int r = nr.getName().lastIndexOf(".class");
				if (r < 0) {
					if (LOG.isInfoEnabled())
						LOG.infof("Resource can't map to Class, Resource %s", nr);
					continue;
				}
				try {
					String className = packagePath.replace('/', '.')
										+ "."
										+ nr.getName()
											.substring(0, r)
											.replace('/', '.')
											.replace('\\', '.');
					Class<?> klass = Lang.loadClass(className);
					re.add(klass);
				}
				catch (ClassNotFoundException e) {
					if (LOG.isInfoEnabled())
						LOG.infof("Resource can't map to Class, Resource %s", nr, e);
				}
			}
		}
		return re;
	}

	public List<NutResource> loadResource(String regex, String... paths) {
		List<NutResource> list = new LinkedList<NutResource>();
		// 解析路径
		for (String path : paths) {
			File f = Files.findFile(path);

			// 如果没找到， 或者是个目录 scan 一下
			if (null == f || f.isDirectory()) {
				list.addAll(scan(path, regex));
			}
			// 普通磁盘文件
			else if (f.isFile()) {
				list.add(new FileResource(f));
			}
			// 存放在 jar 中的文件
			else if (isInJar(f)) {
				NutResource nutResource = makeJarNutResource(f);
				if (nutResource != null) {
					list.add(nutResource);
				} else {
					if (!path.replace('\\', '/').endsWith("/"))
						path += '/';
					list.addAll(scan(path, regex));
				}
			}
		}

		// 如果找不到?
		if (list.size() < 1 && paths.length > 0)
			throw Lang.makeThrow(	RuntimeException.class,
									"folder or file like '%s' no found in %s",
									regex,
									Castors.me().castToString(paths));
		return list;
	}

	public static boolean isInJar(File file) {
		return isInJar(file.getAbsolutePath());
	}
	
	public static boolean isInJar(String filePath) {
		return filePath.contains(".jar!");
	}

	public static NutResource makeJarNutResource(File file) {
		return makeJarNutResource(file.getAbsolutePath());
	}
	
	public static NutResource makeJarNutResource(String filePath) {
		JarEntryInfo jeInfo = new JarEntryInfo(filePath);
		try {
			JarFile jar = new JarFile(jeInfo.getJarPath());
			JarEntry entry = jar.getJarEntry(jeInfo.getEntryName());
			if (entry != null) {
				// JDK里面判断实体是否为文件夹的方法非常不靠谱 by wendal
				if (entry.getName().endsWith("/"))// 明显是文件夹
					return null;
				JarEntry e2 = jar.getJarEntry(jeInfo.getEntryName() + "/");
				if (e2 != null) // 加个/,还是能找到?! 那肯定是文件夹了!
					return null;
				return new JarEntryResource(jeInfo);
			}
		}
		catch (IOException e) {}
		return null;
	}
}
