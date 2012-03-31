package org.nutz.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletContext;

import org.nutz.castor.Castors;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.ClassTools;
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

	private static final Log LOG = Logs.get();

	private Scans() {}

	public static final Scans me() {
		return me;
	}

	private static final String FLT_CLASS = "^.+[.]class$";

	private final ResourceScan local = new LocalResourceScan();

	private WebResourceScan web;

	/**
	 * 在Web环境中使用Nutz的任何功能,都应该先调用这个方法,以初始化资源扫描器
	 * <p/>
	 * 调用一次就可以了
	 */
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
	 * 在jar包中加载基于 src的 package下面的对象
	 * 
	 * @param src
	 * @param regex
	 * @param jarPath
	 * @return
	 * @author mawm at 2012-1-10 下午7:49:48
	 */
	public List<Class<?>> scanPackageInJar(String src, String regexTxt, String jarPath) {

		final Pattern regex = null == regexTxt ? null : Pattern.compile(regexTxt);

		LocalResourceScan lr = (LocalResourceScan) local;

		// 通过local在jar中进行搜索
		List<NutResource> scanInJar = lr.scanInJar(src, regex, jarPath);

		return rs2class(src, scanInJar);
	}

	/**
	 * 在具体位置查找 基于 src的 package下面的对象
	 * 
	 * 
	 * @param src
	 *            需要扫描的包名称
	 * @param regexTxt
	 *            正则表达式
	 * @param fileDir
	 *            源对象所在的磁盘目录
	 * @return
	 * @author replaceToYouName at 2012-1-12 上午9:33:40
	 */
	public List<Class<?>> scanPackageInLocation(String src, String regexTxt, String fileDir) {
		final Pattern regex = null == regexTxt ? null : Pattern.compile(regexTxt);

		LocalResourceScan lr = (LocalResourceScan) local;

		File location = new File(fileDir + "/" + src.replace('.', '/'));
		// 通过local在jar中进行搜索
		List<NutResource> scanInJar = lr.scanInDir(regex, location);

		return rs2class(src, scanInJar);
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
				InputStream in = null;
				try {
					in = nr.getInputStream();
					String className = ClassTools.getClassName(in);
					if (className == null) {
						if (LOG.isInfoEnabled())
							LOG.infof("Resource can't map to Class, Resource %s", nr);
						continue;
					}
					Class<?> klass = Lang.loadClass(className);
					re.add(klass);
				}
				catch (ClassNotFoundException e) {
					if (LOG.isInfoEnabled())
						LOG.infof("Resource can't map to Class, Resource %s", nr, e);
				}
				catch (IOException e) {
					if (LOG.isInfoEnabled())
						LOG.infof("Resource can't map to Class, Resource %s", nr, e);
				} finally {
					Streams.safeClose(in);
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
			ZipInputStream zis = makeZipInputStream(jeInfo.getJarPath());
			ZipEntry ens = null;
			while (null != (ens = zis.getNextEntry())) {
				if (ens.isDirectory())
					continue;
				if (jeInfo.getEntryName().equals(ens.getName())) {
					return makeJarNutResource(zis, ens, "");
				}
			}
		}
		catch (IOException e) {}
		return null;
	}
	
	public static NutResource makeJarNutResource(ZipInputStream zis, ZipEntry ens, String base) throws IOException {
		File entryData = File.createTempFile("nutz.jar.data.", ".bin");
		OutputStream os = Streams.fileOut(entryData);
		long count = ens.getSize();
		byte[] buff = new byte[8192];
		while (count > 0) {
			int len = zis.read(buff);
			count -= len;
			os.write(buff, 0, len);
		}
		os.flush();
		os.close();
		FileResource resource = new FileResource(entryData);
		String name= ens.getName();
		resource.setName(name.substring(base.length()));

		return resource;
	}
	
	public static ZipInputStream makeZipInputStream(String jarPath) throws MalformedURLException, IOException {
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(jarPath));
		} catch (IOException e) {
			zis = new ZipInputStream(new URL(jarPath).openStream());
		}
		return zis;
	}
}
