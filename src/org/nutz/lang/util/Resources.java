package org.nutz.lang.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.lang.Files;

/**
 * 提供了获取资源的一些高级方法
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author Wendal(wendal1985@gmail.com)
 */
public final class Resources {

	/**
	 * It will list all Class object same package with the Class you give.
	 * Whatever the class file you give existed in normal directory or jar file.
	 * 
	 * @param baseClass
	 * @return a class List
	 */
	public static List<Class<?>> scanClass(Class<?> baseClass) {
		File dir = getBasePath(baseClass);
		if (dir == null)
			return null;
		String[] classNames = null;
		String jarPath = getJarPath(dir);
		if (jarPath != null) {
			classNames = findInJar(jarPath, baseClass);
		} else
			classNames = findInClassPath(dir, baseClass);
		if (classNames == null)
			return null;
		List<Class<?>> list = new ArrayList<Class<?>>(classNames.length);
		for (String className : classNames)
			try {
				list.add(Class.forName(className));
			} catch (Throwable e) {}
		return list;
	}

	/**
	 * Find a file base on one class Object.
	 * <p>
	 * please check File getBasePath(String base) for more detail
	 * 
	 * 
	 * @param classZ
	 * @return the class package directory.
	 */
	private static File getBasePath(Class<?> classZ) {
		return getBasePath(classZ.getName().replace('.', '/') + ".class");
	}

	/**
	 * The function try to return the file path of one class or package. If it
	 * exists in regular directory, it will return as
	 * "D:/folder/folder/name.class" in windows, and "/folder/folder/name.class"
	 * in unix like system.
	 * <p>
	 * If the class file exists in one jar file, it will return the path like:
	 * <b>'XXXXXXXfile:\XXXXXX\XXX.jar!\XX\XX\XX'</b>
	 * <p>
	 * use ClassLoader.getResources(String) to search resources in classpath
	 * <p>
	 * <b style=color:red>Note:</b>
	 * <p>
	 * We use new <i>ClassLoader(){}</i> to instead of
	 * <i>classZ.getClassLoader()</i>, for the reason: in <b>GAE</b> , it will
	 * fail if you call getClassLoader() <br>
	 * <br>
	 * 
	 * @param base
	 *            : the class file name or package dir name
	 * @return path or null if nothing found
	 * 
	 * @author Wendal Chen
	 * @author zozoh
	 * 
	 * @see java.lang.ClassLoader
	 * @see java.io.File
	 */
	private static File getBasePath(String base) {
		try {
			Enumeration<URL> urls = new ClassLoader() {}.getResources(base);
			File file = null;
			// zozoh: In eclipse tomcat debug env, the urls is always empty
			if (null != urls && urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null) {
					String path = url.getFile();
					// If there is some whitespace in path, should decode it.
					path = decodePath(path);
					file = new File(path);
				}
			}
			// Then I will find the class in classpath
			if (null == file)
				file = Files.findFile(base);

			if (null == file)
				return null;
			// If the base is folder return it directly, else, return it's
			// parent folder
			try {
				if (file.isDirectory())
					return file;
			} catch (SecurityException e) {
				// In GAE , it will happen.
			}
			return file.getParentFile();
		} catch (IOException e) {}
		return null;
	}

	private static String[] findInJar(String jarPath, Class<?> baseClass) {
		try {
			jarPath = decodePath(jarPath);
			ZipEntry[] entrys = Files.findEntryInZip(new ZipFile(jarPath), baseClass.getPackage()
					.getName().replace('.', '/')
					+ "/\\w*.class");
			if (null != entrys && entrys.length > 0) {
				String[] classNames = new String[entrys.length];
				for (int i = 0; i < entrys.length; i++) {
					String ph = entrys[i].getName();
					classNames[i] = ph.substring(0, ph.lastIndexOf('.'))
							.replaceAll("[\\\\|/]", ".");
				}
				return classNames;
			}
		} catch (IOException e) {}
		return null;
	}

	private static String[] findInClassPath(File dir, Class<?> classZ) {
		try {
			File[] files = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".class");
				}
			});
			if (null != files && files.length > 0) {
				String[] classNames = new String[files.length];
				Package packageA = classZ.getPackage();
				for (int i = 0; i < files.length; i++) {
					String fileName = files[i].getName();
					String classShortName = fileName.substring(0, fileName.length()
							- ".class".length());
					classNames[i] = packageA.getName() + "." + classShortName;
				}
				return classNames;
			}
		} catch (SecurityException e) {
			// In GAE, it will case SecurityException when call listFiles()
		}
		return null;
	}

	private static String getJarPath(File dir) {
		String fpath = dir.getAbsolutePath();
		int posBegin = fpath.indexOf("file:") + 5;
		int posEnd = fpath.lastIndexOf('!');
		if (posBegin > 0 && (posEnd - posBegin) > 0)
			return fpath.substring(posBegin, posEnd);
		return null;
	}

	private static String decodePath(String path) {
		try {
			return URLDecoder.decode(path, Charset.defaultCharset().name());
		} catch (UnsupportedEncodingException e) {}
		return path;
	}
}
