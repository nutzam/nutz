package org.nutz.lang.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 加载资源,支持从jar中加载相关文件
 * 
 * @author mawm(ming300@gmail.com)
 */
public class ClassLoaderUtil {
	private static final Log log = Logs.getLog(ClassLoaderUtil.class);

	private static URL getResource1(String resourceName, Class<?> callingClass) {
		if (null == resourceName || "".equals(resourceName)) {
			return null;
		}
		URL url = null;
		url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
		if (url == null)
			try {
				File tmpFile = new File(resourceName);
				if (tmpFile.exists())
					url = (new File(resourceName)).toURI().toURL();
			}
			catch (MalformedURLException malformedurlexception) {}
		try {
			if (url == null)
				url = ClassLoaderUtil.class.getClassLoader().getResource(resourceName);
		}
		catch (Exception exception) {}
		try {
			if (url == null)
				url = callingClass.getClassLoader().getResource(resourceName);
		}
		catch (Exception exception1) {}
		try {
			if (url == null) {
				File tmpFile = new File(System.getProperty("user.dir")
										+ File.separator
										+ resourceName);
				if (tmpFile.exists())
					url = (new File(System.getProperty("user.dir") + File.separator + resourceName)).toURI()
																									.toURL();
			}
		}
		catch (Exception exception2) {}
		if (url == null && resourceName != null && resourceName.charAt(0) != '/')
			return getResource('/' + resourceName, callingClass);
		else
			return url;

	}

	public static URL getResource(String resourceName, Class<?> callingClass) {
		URL url = getResource1(resourceName, callingClass);

		// StringBuffer txt1 = new StringBuffer();
		// txt1.append("ļ").append(resourceName).append("·:").append(url);
		// System.err.println(txt1);

		return url;
	}

	public static InputStream getResourceAsStream(String resourceName, Class<?> callingClass) {
		URL url = getResource(resourceName, callingClass);
		try {
			return url != null ? url.openStream() : null;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Class<?> loadClass(String className, Class<?> callingClass)
			throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException e) {}
		try {
			return Class.forName(className);
		}
		catch (ClassNotFoundException e2) {}
		try {
			return ClassLoaderUtil.class.getClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException e3) {
			return callingClass.getClassLoader().loadClass(className);
		}
	}

	/**
	 * 加载Java类。 使用全限定类名
	 * 
	 * @param className
	 * @return
	 */
	public static Class<?> loadClass(String className) {
		try {
			return getClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("class not found '" + className + "'", e);
		}
	}

	/**
	 * *得到类加载器
	 * 
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return ClassLoaderUtil.class.getClassLoader();
	}

	/**
	 * *提供相对于classpath的资源路径，返回文件的输入流
	 * 
	 * @param relativePath
	 *            必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使用 ../来查找
	 * @return 文件输入流
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static InputStream getStream(String relativePath) throws MalformedURLException,
			IOException {
		if (!relativePath.contains("../")) {
			return getClassLoader().getResourceAsStream(relativePath);
		} else {
			return ClassLoaderUtil.getStreamByExtendResource(relativePath);
		}
	}

	/**
	 * openStream
	 * 
	 * @param url
	 * @return openStream
	 * @throws IOException
	 */
	public static InputStream getStream(URL url) throws IOException {
		if (url != null) {
			return url.openStream();
		} else {
			return null;
		}
	}

	/**
	 * *
	 * 
	 * @paramr elativePath必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使用
	 *         ../来查找
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static InputStream getStreamByExtendResource(String relativePath)
			throws MalformedURLException, IOException {
		return ClassLoaderUtil.getStream(ClassLoaderUtil.getExtendResource(relativePath));
	}

	/**
	 * *提供相对于classpath的资源路径，返回属性对象，它是一个散列表
	 * 
	 * @param resource
	 * @return
	 */
	public static Properties getProperties(String resource) {
		Properties properties = new Properties();
		try {
			properties.load(getStream(resource));
		}
		catch (IOException e) {
			throw new RuntimeException("couldn't load properties file '" + resource + "'", e);
		}
		return properties;
	}

	/**
	 * *得到本Class所在的ClassLoader的Classpat的绝对路径。 *URL形式的
	 * 
	 * @return
	 */
	public static String getAbsolutePathOfClassLoaderClassPath() {
		if (log.isDebugEnabled()) {
			log.debug(ClassLoaderUtil.getClassLoader()

			.getResource("").toString());
		}
		return ClassLoaderUtil.getClassLoader().getResource("").toString();
	}

	/**
	 * *
	 * 
	 * @param relativePath
	 *            必须传递资源的相对路径。是相对于classpath的路径。如果需要查找classpath外部的资源，需要使 用../来查找
	 * @return 资源的绝对URL
	 * @throws MalformedURLException
	 */
	public static URL getExtendResource(String relativePath) throws MalformedURLException {
		if (log.isDebugEnabled()) {
			log.debug("传入的相对路径：" + relativePath);
		}
		// if (log.isDebugEnabled())
		// {log.debug(Integer.valueOf(relativePath.indexOf("../")))
		// ;
		if (!relativePath.contains("../")) {
			return ClassLoaderUtil.getResource(relativePath);
		}
		String classPathAbsolutePath = ClassLoaderUtil.getAbsolutePathOfClassLoaderClassPath();
		if (relativePath.substring(0, 1).equals("/")) {
			relativePath = relativePath.substring(1);
		}
		// if (log.isDebugEnabled()) {
		// log.debug(Integer.valueOf(relativePath
		// .lastIndexOf("../")));
		// }

		String wildcardString = relativePath.substring(0, relativePath.lastIndexOf("../") + 3);
		relativePath = relativePath.substring(relativePath.lastIndexOf("../") + 3);
		int containSum = ClassLoaderUtil.containSum(wildcardString, "../");
		classPathAbsolutePath = ClassLoaderUtil.cutLastString(	classPathAbsolutePath,
																"/",
																containSum);
		String resourceAbsolutePath = classPathAbsolutePath + relativePath;
		if (log.isDebugEnabled()) {
			log.debug("绝对路径：" + resourceAbsolutePath);
		}
		URL resourceAbsoluteURL = new URL(resourceAbsolutePath);
		return resourceAbsoluteURL;
	}

	/**
	 * *
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	private static int containSum(String source, String dest) {
		int containSum = 0;
		int destLength = dest.length();
		while (source.contains(dest)) {
			containSum = containSum + 1;
			source = source.substring(destLength);
		}
		return containSum;
	}

	/**
	 * @param source
	 * @param dest
	 * @param num
	 * @return
	 * @author mawenming at 2010-4-10 上午10:02:51
	 */
	private static String cutLastString(String source, String dest, int num) {
		// String cutSource=null;
		for (int i = 0; i < num; i++) {
			source = source.substring(0, source.lastIndexOf(dest, source.length() - 2) + 1);
		}
		return source;
	}

	/**
	 * *
	 * 
	 * @param resource
	 * @return
	 */
	public static URL getResource(String resource) {
		if (log.isDebugEnabled()) {
			log.debug("传入的相对于classpath的路径：" + resource);
		}
		return ClassLoaderUtil.getClassLoader().getResource(resource);
	}

	public static void main(String[] args) throws MalformedURLException {
		// ClassLoaderUtil.getExtendResource("../spring/dao.xml");
		// ClassLoaderUtil.getExtendResource("../../../src/log4j.properties");
		ClassLoaderUtil.getExtendResource("log4j.properties");
		URL rs = ClassLoaderUtil.getExtendResource("../");
		;
		log.debug("rs=" + rs.getPath());

		ClassLoaderUtil.getExtendResource("../config/freemarkettemplate/a/example.ftl");
		System.out.println(ClassLoaderUtil	.getClassLoader()
											.getResource("log4j.properties")
											.toString());

		System.out.println("Thread.currentThread().getContextClassLoader()=\n"
							+ Thread.currentThread().getContextClassLoader().getResource(""));
		System.out.println("ClassLoaderUtil.class.getClassLoader().getResource=\n"
							+ ClassLoaderUtil.class.getClassLoader().getResource(""));
		System.out.println("ClassLoader.getSystemResource=\n" + ClassLoader.getSystemResource(""));
		System.out.println("ClassLoaderUtil.class.getResource=\n"
							+ ClassLoaderUtil.class.getResource(""));
		System.out.println("ClassLoaderUtil.class.getResource(\"/\")=\n"
							+ ClassLoaderUtil.class.getResource("/"));

		System.out.println("new File(\"/\").getAbsolutePath()=\n" + new File("").getAbsolutePath());

		System.out.println("new File(\"/\").getAbsolutePath()=\n" + new File("/").getAbsolutePath());
		System.out.println("System.getProperty(\"user.dir\")=\n" + System.getProperty("user.dir"));

		log.debug("qq ip =" + ClassLoaderUtil.getExtendResource("../ipdata/QQWry.Dat"));

		log.debug("getWebRoot() =" + getWebRoot());

	}

	/**
	 * 服务器的WEB-ROOT目录地址
	 * 
	 * @author mawenming at 2010-4-10 上午10:01:54
	 */
	private static String WEB_ROOT = null;

	/**
	 * 服务器的WEB-INF目录地址
	 * 
	 * @author mawenming at 2010-4-10 上午10:02:24
	 */
	private static String WEB_INF_PATH = null;

	/**
	 * @return
	 * @author mawenming at Apr 24, 2009 2:15:03 PM
	 */
	public static String getWEB_INFPath() {
		if (WEB_INF_PATH == null) {
			try {
				URL url = ClassLoaderUtil.getExtendResource("../");
				if (url != null) {
					WEB_INF_PATH = url.getFile();
					return WEB_INF_PATH;
				}
			}
			catch (MalformedURLException ex) {
				log.error("", ex);
				return "";
			}
			return "";
		} else {
			return WEB_INF_PATH;
		}
	}

	/**
	 * @return
	 * @author mawenming at Apr 24, 2009 2:15:03 PM
	 */
	public static String getWebRoot() {
		if (WEB_ROOT == null) {
			try {
				URL url = ClassLoaderUtil.getExtendResource("../../");
				if (url != null) {
					WEB_ROOT = url.getFile();
					return WEB_ROOT;
				}
			}
			catch (MalformedURLException ex) {
				log.error("", ex);
				return "";
			}
			return "";
		} else {
			return WEB_ROOT;
		}
	}

	/**
	 * 判断 类是否存在
	 * 
	 * @param className
	 * @param classLoader
	 * @return
	 */
	public static boolean isPresent(String className, ClassLoader classLoader) {
		if (Strings.isEmpty(className))
			return false;
		try {
			if (classLoader == null) {
				classLoader = getClassLoader();
			}
			classLoader.loadClass(className);
			return true;
		}
		catch (Throwable ex) {
			return false;
		}

	}
}