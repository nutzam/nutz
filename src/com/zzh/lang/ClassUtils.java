package com.zzh.lang;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


public final class ClassUtils {

	// the excluded packages skip to find in the packages
	// it will skip all subpackages
	private static String[] excludedPackages = { "java.", "javax.", "com.sun.", "org.", "sun.",
			"sunw." };

	public static void setExcludedPackages(String excludedPackagesStr) {
		excludedPackages = Strings.splitIgnoreBlank(excludedPackagesStr);
	}

	/**
	 * Find in the class path the first class implementing the given interface
	 * (or abstract class).
	 * 
	 * @param <T>
	 * 
	 * @param researchedInterface
	 *            - The interface or the abstract class.
	 * @param classLoader
	 *            The classLoader for loading the Implementation class.
	 * @return - The implementation class if found.
	 */
	public static <T> List<Class<T>> findImplementationClassesInClasspath(
			Class<T> researchedInterface, ClassLoader classLoader) {

		File[] classpathes = getClasspathes();

		return findImplementClassesInFiles(researchedInterface, classLoader, classpathes);
	}

	public static <T> List<Class<T>> findImplementClassesInFiles(Class<T> researchedInterface,
			ClassLoader classLoader, File[] classpathes) {
		// check parameters
		if (researchedInterface == null) {
			throw new NullPointerException("researchedInterface");
		}
		if (classLoader == null) {
			throw new NullPointerException("classLoader");
		}

		// The classes implemenation researshed.
		List<Class<T>> classes = new LinkedList<Class<T>>();

		// iterate the object in classpath.
		for (File classpath : classpathes) {

			if (classpath.isDirectory()) {
				try {
					classes
							.addAll(findInDirectory(classpath, "", researchedInterface, classLoader));
				} catch (Throwable t) {
					// ignoring, continue to search in another classpath
					// element.
					// t.printStackTrace();
				}
			} else if (classpath.exists()
					&& (classpath.getName().endsWith(".zip") || classpath.getName()
							.endsWith(".jar"))) {
				try {
					classes.addAll(findInZip(classpath, researchedInterface, classLoader));
				} catch (Throwable t) {
					// ignoring, continue to search in another classpath
					// element.
					// t.printStackTrace();
				}
			}
		}
		return classes;
	}

	private static File[] getClasspathes() {
		String classpath = System.getProperty("java.class.path");
		String[] classpathes = classpath.split(File.pathSeparator);
		File[] files = null;
		if (null != classpathes && classpathes.length > 0) {
			files = new File[classpathes.length];
		}
		for (int i = 0; i < classpathes.length; i++) {
			files[i] = new File(classpathes[i]);
		}
		return files;
	}

	private static <T> List<Class<T>> findInDirectory(File directory, String pckgname,
			Class<T> researchedInterface, ClassLoader classLoader) {

		List<Class<T>> classes = new LinkedList<Class<T>>();
		// check if need to filter
		if (!isExcludedPackage(pckgname)) {
			if (directory.exists()) {
				String[] files = directory.list();

				for (String file : files) {
					// we are only interested in .class files
					if (file.endsWith(".class")) {
						Class<T> clazz = findClass(file, pckgname, researchedInterface, classLoader);
						if (clazz != null) {
							classes.add(clazz);
						}
					} else {
						// We will iterate for the following package :
						String packageName = pckgname;

						// The sub dir to search into:
						String subDirName = new String(directory.getAbsolutePath()
								+ File.separatorChar + file);
						java.io.File subDir = new java.io.File(subDirName);

						if (subDir.isDirectory()) {
							// test if it is the first package:
							if (packageName.length() != 0) {
								packageName = packageName + "." + file;
							} else {
								packageName = file;
							}
							// find in sub directory :
							classes.addAll(findInDirectory(subDir, packageName,
									researchedInterface, classLoader));
						}
					}
				}
			}
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	private static <T> List<Class<T>> findInZip(File zipFile, Class<T> researchedInterface,
			ClassLoader classLoader) throws ZipException, IOException {

		List<Class<T>> classes = new LinkedList<Class<T>>();

		ZipFile zipfile = null;
		zipfile = new ZipFile(zipFile);

		// search a good entry in the zip file:
		for (Enumeration<?> enumeration = zipfile.entries(); enumeration.hasMoreElements();) {
			ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
			final String entryName = zipEntry.getName();
			if (entryName.endsWith(".class")) {
				// a name of a ZipEntry : org/nutagi/SB
				String className = entryName.replace('/', '.');
				// remove the ".class" extension
				className = className.substring(0, className.length() - 6);
				// check if need to filter
				if (!isExcludedPackage(className)) {
					try {
						Class<?> clazz = classLoader.loadClass(className);
						if (researchedInterface.isAssignableFrom(clazz)) {
							classes.add((Class<T>) clazz);
						}
					} catch (Throwable t) {
					}
				}
			}
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> findClass(String file, String pckgname,
			Class<T> researchedInterface, ClassLoader classLoader) {
		// removes the .class extension
		String className = file.substring(0, file.length() - 6);
		try {
			Class<?> clazz = null;
			clazz = classLoader.loadClass(pckgname + "." + className);
			if (researchedInterface.isAssignableFrom(clazz)) {
				return (Class<T>) clazz;
			}
		} catch (ClassNotFoundException cnfex) {
		}
		return null;
	}

	private static boolean isExcludedPackage(String packageStr) {
		if (null == packageStr)
			return true;
		if (null != excludedPackages) {
			for (String filter : excludedPackages) {
				if (packageStr.startsWith(filter))
					return true;
			}
		}
		return false;
	}
}
