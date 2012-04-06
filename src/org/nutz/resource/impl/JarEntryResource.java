package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nutz.resource.NutResource;

/**
 * 封装了 jar 内的 Entity
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JarEntryResource extends NutResource {

	private String jarPath;
	private String entryName;

	public JarEntryResource(JarFile jar, JarEntry jen, String name) {
		this.jarPath = jar.getName();
		this.entryName = jen.getName();
		this.name = name;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		JarFile jarFile = new JarFile(jarPath);
		return jarFile.getInputStream(jarFile.getEntry(entryName));
	}

	@Override
	public String toString() {
		return String.format("JarEntryResource[%s] jarPath[%s]", entryName, jarPath);
	}
}
