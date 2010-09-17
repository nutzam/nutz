package org.nutz.resource.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;

/**
 * 只适用于标准的Web结构的资源扫描
 * 
 * @author Wendal(wendal1985@gmail.com)
 * 
 */
@SuppressWarnings("unchecked")
public class WebResourceScan extends AbstractResourceScan {

	private static final Log log = Logs.getLog(WebResourceScan.class);

	private static final String WEB_CLASSES = "/WEB-INF/classes/";

	private static final String WEB_LIB = "/WEB-INF/lib/";

	private ServletContext sc;

	public WebResourceScan(ServletContext servletContext) {
		this.sc = servletContext;
	}

	public List<NutResource> list(final String src, String filter) {
		final Pattern regex = Pattern.compile(filter);
		final List<NutResource> list = new ArrayList<NutResource>();
		// 获取全部jar
		Set<String> jars = sc.getResourcePaths(WEB_LIB);
		for (String path : jars) {
			log.info("Scan file --> " + path);
			try {
				JarFile jar = new JarFile(sc.getRealPath(path));
				Enumeration<JarEntry> ens = jar.entries();
				while (ens.hasMoreElements()) {
					JarEntry jen = ens.nextElement();
					String name = jen.getName();
					if (name.startsWith(src) && regex.matcher(name).find()) {
						list.add(new JarEntryResource(jar, jen));
					}
				}
			}
			catch (Throwable e) {
				if (log.isFatalEnabled())
					log.fatal("Fail to scan path '" + path + "'!", e);
			}
		}
		// 获取classes里面文件
		// 对 classes 文件夹作一个深层遍历
		// 忽略隐藏文件，以不能被 filter 匹配的项目
		// 返回的 NutResource 对象，都是以 classes 目录为根
		File dir = new File(sc.getRealPath(WEB_CLASSES));
		// 目录不存在
		if (!dir.exists()) {
			if (log.isWarnEnabled())
				log.warnf(	"Fail to found '%s' in context [%s]",
							WEB_CLASSES,
							sc.getServletContextName());
		}
		// 不是一个目录
		else if (!dir.isDirectory()) {
			if (log.isWarnEnabled())
				log.warnf(	"'%s' in context [%s] should be a directory!",
							WEB_CLASSES,
							sc.getServletContextName());
		}
		// 那么很好，深层递归一下吧
		else {
			if (log.isDebugEnabled())
				log.debugf("Scan in web classes : %s",dir);
			final int pos = dir.getAbsoluteFile().getAbsolutePath().length() + 1;
			Disks.visitFile(dir,
			/*
			 * 处理文件
			 */
			new FileVisitor() {
				public void visit(File file) {
					String name = file.getAbsoluteFile().getAbsolutePath().substring(pos);
					if (name.startsWith(src))
						list.add(new ClasspathResource(name));
				}
			},
			/*
			 * 文件需要满足的条件
			 */
			new FileFilter() {
				public boolean accept(File f) {
					if (f.isHidden())
						return false;
					if (f.isDirectory())
						return true;
					return regex.matcher(f.getName()).find();
				}
			});
		}

		return list;
	}

	public boolean canWork() {
		return false;
	}

	static class ClasspathResource extends NutResource {

		public ClasspathResource(String name) {
			this.name = name;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
		}

	}
}
