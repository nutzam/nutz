package org.nutz.resource.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.nutz.lang.Files;
import org.nutz.lang.util.Disks;
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

	private static final Log log = Logs.get();

	private ServletContext sc;

	public WebResourceScan(ServletContext servletContext) {
		this.sc = servletContext;
	}

	public List<NutResource> list(final String src, String filter) {
		final Pattern regex = null == filter ? null : Pattern.compile(filter);
		final List<NutResource> list = new ArrayList<NutResource>();
		// 获取全部jar
		Set<String> jars = sc.getResourcePaths("/WEB-INF/lib/");
		if(jars != null) //这个文件夹不一定存在,尤其是Maven的WebApp项目
			for (String path : jars) {
				if (!path.toLowerCase().endsWith(".jar"))
					continue;
				list.addAll(scanInJar(checkSrc(src), regex, sc.getRealPath(path)));
			}
		// 获取classes里面文件
		// 对 classes 文件夹作一个深层遍历
		// 忽略隐藏文件，以不能被 filter 匹配的项目
		// 返回的 NutResource 对象，都是以 classes 目录为根
		File dir = Files.findFile(src);
		boolean flag = true;
		if (null != dir && dir.exists()) {
			// 获取 CLASSPATH 的基目录
			String src2 = Disks.getCanonicalPath(src);
			String dirPath = Disks.getCanonicalPath(dir.getAbsolutePath());
			int pos = dirPath.indexOf(src2, dirPath.indexOf("classes") + 7);
			final String base = pos < 0 ? dirPath : dirPath.substring(0, pos);

			// 那么很好，深层递归一下吧
			if (log.isDebugEnabled())
				log.debugf("Scan in web classes : %s , base = %s", dir, base);

			List<NutResource> list2 = scanInDir(regex, base, dir, true);
			for (NutResource nutResource : list2) {
				String name = nutResource.getName();
				if (name.indexOf(base) > -1)
					nutResource.setName(name.substring(base.length()));
				list.add(nutResource);
			}
			flag = list2.isEmpty();
		}
		// 目录不存在,或者里面没有任何文件
		if (flag && (!src.startsWith("/"))) {
			try {
				String base = sc.getRealPath("/WEB-INF/classes/");
				String path = sc.getRealPath("/WEB-INF/classes/" + src);
				if (path != null) {
					List<NutResource> list2 = scanInDir(regex, base, new File(
							path), true);
					for (NutResource nutResource : list2) {
						String name = nutResource.getName();
						if (name.indexOf(base) > -1)
							nutResource.setName(name.substring(base.length()));
						list.add(nutResource);
					}
					flag = list2.isEmpty();
				}
			} catch (Throwable e) {
			}
		}
		// 还是空? 查一下classpath变量吧, Fix Issue 411
		if (flag) {
			String classpath = System.getProperties().getProperty(
					"java.class.path");
			if (log.isInfoEnabled())
				log.info("Try to search in classpath : " + classpath);
			String[] paths = classpath.split(System.getProperties()
					.getProperty("path.separator"));
			for (String pathZ : paths) {
				if (pathZ.endsWith(".jar"))
					list.addAll(scanInJar(checkSrc(src), regex, pathZ));
				else
					list.addAll(scanInDir(regex, pathZ, new File(pathZ + "/"
							+ src),true));
			}
			flag = list.isEmpty();
		}
		if (flag && log.isInfoEnabled())
			log.infof("Fail to found '%s' in /WEB-INF/classes of context [%s]",
					src, sc.getServletContextName());
		return list;
	}

}
