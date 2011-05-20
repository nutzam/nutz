package org.nutz.resource.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;

public abstract class AbstractResourceScan implements ResourceScan {

	private static final Log log = Logs.get();

	protected List<NutResource> scanInJar(String src, Pattern regex, String jarPath) {
		List<NutResource> list = new ArrayList<NutResource>();
		try {
			if (log.isDebugEnabled())
				log.debugf(	"Scan resources in JarFile( %s ) by regex( %s ) base on src ( %s )",
							jarPath,
							regex,
							src);
			JarFile jar = new JarFile(jarPath);
			Enumeration<JarEntry> ens = jar.entries();
			while (ens.hasMoreElements()) {
				JarEntry jen = ens.nextElement();
				if (jen.isDirectory())
					continue;
				String name = jen.getName();
				if (name.startsWith(src) && (null == regex || regex.matcher(name).find())) {
					list.add(new JarEntryResource(jar, jen, jen.getName().substring(src.length())));
				}
			}
			if (log.isDebugEnabled())
				log.debugf(	"Found %s resources in JarFile( %s ) by regex( %s ) base on src ( %s )",
							list.size(),
							jarPath,
							regex,
							src);
		}
		catch (Throwable e) {
			if (log.isWarnEnabled())
				log.warn("Fail to scan path '" + jarPath + "'!", e);
		}
		return list;
	}

	/* 存在两种调用,有的需要得出的Resouce包含原始的base,有些却不需要 */
	protected List<NutResource> scanInDir(final Pattern regex,
	// final String base,
											File f,
											final boolean ignoreHidden) {
		final List<NutResource> list = new ArrayList<NutResource>();
		if (null == f || (ignoreHidden && f.isHidden()) || (!f.exists()))
			return list;

		if (!f.isDirectory())
			f = f.getParentFile();
		final String base = f.getAbsolutePath();
		Disks.visitFile(f, new FileVisitor() {
			public void visit(File file) {
				list.add(new FileResource(base, file));
			}
		}, new FileFilter() {
			public boolean accept(File theFile) {
				if (ignoreHidden && theFile.isHidden())
					return false;
				if (theFile.isDirectory()) {
					String fnm = theFile.getName().toLowerCase();
					// 忽略 SVN 和 CVS 文件
					if (".svn".equals(fnm) || ".cvs".equals(fnm))
						return false;
					return true;
				}
				return regex == null || regex.matcher(theFile.getName()).find();
			}
		});

		return list;
	}

	protected static String checkSrc(String src) {
		if (src == null)
			return null;
		src = src.replace('\\', '/');
		if (!src.endsWith("/"))
			src += "/";
		return src;
	}
}
