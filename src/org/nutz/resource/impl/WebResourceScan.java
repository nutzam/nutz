package org.nutz.resource.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletContext;

import org.nutz.resource.NutResource;

public class WebResourceScan extends AbstractResourceScan {
	
	private static final String WebClasses = "/WEB-INF/classes";

	private static final String WebLib = "/WEB-INF/lib";
	
	private ServletContext servletContext;
	
	public WebResourceScan(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@SuppressWarnings("unchecked")
	public List<NutResource> list(String src, String filter) {
		List<NutResource> list = new ArrayList<NutResource>();
		//获取全部jar
		Set<String> jars = servletContext.getResourcePaths(WebLib);
		for (String path : jars) {
			try {
				JarFile jarFile = new JarFile(path);
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = entries.nextElement();
					String name = jarEntry.getName();
					if (name.startsWith(src) && name.endsWith(filter)) {
						JarEntryResource resource = new JarEntryResource(path, name);
						list.add(resource);
					}
				}
			}catch (Throwable e) {
				e.printStackTrace();
			}
		}
		//获取classes里面文件
		Set<String> set = lists(WebClasses);
		for (String filePath : set) {
			String simpleName = filePath.substring(filePath.lastIndexOf(WebClasses) + WebClasses.length());
			if (simpleName.startsWith(src) && simpleName.endsWith(filter)) {
				FileResource resource = new FileResource(filePath, simpleName);
				list.add(resource);
			}
		}
		return list;
	}

	public boolean canWork() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> lists(String path){
		Set<String> set = new HashSet<String>();
		if (path.endsWith("/"))
			set.add(path);
		else{
			Set<String> pps = servletContext.getResourcePaths(path);
			if (pps != null && pps.size() > 0)
				for (String str : pps)
					set.addAll(lists(str));
		}
		return set;
	}

	static class JarEntryResource extends NutResource {

		private String jarEntryName;

		private String jarFileName;

		public JarEntryResource(String jarFileName, String jarEntryName) {
			super();
			this.jarFileName = jarFileName;
			this.jarEntryName = jarEntryName;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			ZipFile zipFile = new ZipFile(jarFileName);
			ZipEntry zipEntry = zipFile.getEntry(jarEntryName);
			return zipFile.getInputStream(zipEntry);
		}

		@Override
		public String getName() {
			return this.jarEntryName;
		}
	}
	
	static class FileResource extends NutResource {

		private String filePath;

		public FileResource(String filePath, String name) {
			this.filePath = filePath;
			this.name = name;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new FileInputStream(filePath);
		}

	}
}
