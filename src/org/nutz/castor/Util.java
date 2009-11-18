package org.nutz.castor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.nutz.lang.Files;

/**
 * 
 * @author wendal Chen
 *
 */
public final class Util {
	
	public static List<Class<?>> scanClass(Class<?> baseClass){
		File dir = getBasePath(baseClass);
		if(dir == null)
			return null;
		String[] classNames = null;
		String jarPath = getJarPath(dir);
		if(jarPath != null){
			classNames = findInJar(jarPath,baseClass);
		}else
			classNames = findInClassPath(dir,baseClass);
		if(classNames != null){
			List<Class<?>> list = new ArrayList<Class<?>>(classNames.length);
			for (String className : classNames)
				try {
					list.add(Class.forName(className));
				} catch (Throwable e) {}
			return list;
		}
		return null;
	}

	/**
	 * The function try to return the file path of one class. If it exists in
	 * regular directory, it will return as "D:/folder/folder/name.class" in
	 * windows, and "/folder/folder/name.class" in unix like system. <br>
	 * If the class file exists in one jar file, it will return the path like:
	 * "XXXXXXfile:\XXXXXX\XXX.jar!\XX\XX\XX"
	 * <p>
	 * use ClassLoader.getResources(String) to search resources in classpath
	 * <p>
	 * <b>Using new ClassLoader(){} , not classZ.getClassLoader()</b>
	 * <p>
	 * In GAE , it will fail if you call getClassLoader()
	 * 
	 * @author Wendal Chen
	 * @author zozoh
	 * @param classZ
	 * @return path or null if nothing found
	 * 
	 * @see java.lang.ClassLoader
	 * @see java.io.File
	 */
	private static File getBasePath(Class<?> classZ) {
		try {
			String path = classZ.getName().replace('.', '/') + ".class";
			Enumeration<URL> urls = new ClassLoader() {}.getResources(path);
			// zozoh: In eclipse tomcat debug env, the urls is always empty
			if (null != urls && urls.hasMoreElements()) {
				URL url = urls.nextElement();
				if (url != null)
					return new File(url.getFile()).getParentFile();
			}
			// Then I will find the class in classpath
			File f = Files.findFile(path);
			if (null != f)
				return f.getParentFile();
		} catch (IOException e) {}
		return null;
	}
	
	private static String[] findInJar(String jarPath,Class<?> baseClass) {
		try {
			ZipEntry[] entrys = Files.findEntryInZip(new ZipFile(jarPath), baseClass
					.getPackage().getName().replace('.', '/')
					+ "/\\w*.class");
			if (null != entrys && entrys.length > 0) {
				String[] classNames = new String[entrys.length];
				for (int i = 0; i < entrys.length; i++) {
					String ph = entrys[i].getName();
					classNames[i] = ph.substring(0, ph.lastIndexOf('.')).replaceAll("[\\\\|/]",
								".");
				}
				return classNames;
			}
		} catch (IOException e) {}
		return null;
	}

	private static String[] findInClassPath(File dir,Class<?> classZ) {
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
	
	private static String getJarPath(File dir){
		String fpath = dir.getAbsolutePath();
		int posBegin = fpath.indexOf("file:");
		int posEnd = fpath.lastIndexOf('!');
		if (posBegin > 0 && (posEnd - posBegin - 5) > 0) {
			return fpath.substring(posBegin + 5, posEnd);
		}
		return null;
	}
}
