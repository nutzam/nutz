package org.nutz.resource.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.JarEntryInfo;
import org.nutz.resource.NutResource;
import org.nutz.resource.ResourceScan;
import org.nutz.resource.Scans;

public abstract class AbstractResourceScan implements ResourceScan {

	private static final Log log = Logs.get();

	public List<NutResource> scanInJar(String src, Pattern regex, String jarPath) {
		List<NutResource> list = new ArrayList<NutResource>();
		try {
			if (log.isDebugEnabled())
				log.debugf(	"Scan resources in JarFile( %s ) by regex( %s ) base on src ( %s )",
							jarPath,
							regex,
							src);
			//先尝试普通方式
			try {
				JarFile jarFile = new JarFile(new File(jarPath));
				Enumeration<JarEntry> ens = jarFile.entries();
				while(ens.hasMoreElements()) {
					JarEntry entry = ens.nextElement();
					String name = entry.getName();
					if (!entry.isDirectory() && entry.getName().startsWith(src)) {
						if (regex != null && !regex.matcher(name).find())
							continue;
						list.add(new JarEntryResource(jarFile, entry, name));
					}
				}
			} catch (IOException e) {
				//换成万能方式
				ZipInputStream zis = new ZipInputStream(new URL(jarPath).openStream());
				ZipEntry ens = null;
				while (null != (ens = zis.getNextEntry())) {
					if (ens.isDirectory())
						continue;
					String name = ens.getName();
					if (name.startsWith(src) && (null == regex || regex.matcher(name).find())) {
						list.add(Scans.makeJarNutResource(jarPath, name, src));
					}
				}
				zis.close();
			}
			
			if (list.size() > 0 && log.isDebugEnabled())
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
					// 忽略 SVN 和 CVS 文件,还有Git文件
					if (".svn".equals(fnm) || ".cvs".equals(fnm) || ".git".equals(fnm))
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
	
	protected void scanClasspath(String src, Pattern regex, List<NutResource> list) {
		try {
			String classpath = System.getProperties().getProperty("java.class.path");
			if (log.isInfoEnabled())
				log.info("Try to search in classpath : " + classpath);
			String[] paths = classpath.split(System.getProperties().getProperty("path.separator"));
			for (String pathZ : paths) {
				if (pathZ.endsWith(".jar"))
					list.addAll(scanInJar(checkSrc(src), regex, pathZ));
				else
					list.addAll(scanInDir(regex, new File(pathZ + "/" + src), true));
			}
		} catch (Throwable e) {}
		//这是最后的手段了
		try {
			Enumeration<URL> urls = getClass().getClassLoader().getResources("/META-INF/MANIFEST.MF");
			while (urls.hasMoreElements()) {
				list.addAll(scanInJar(src, regex, new JarEntryInfo(urls.nextElement().toString()).getJarPath()));
			}
		} catch (Throwable e) {}
	}
}
