package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nutz.resource.JarEntryInfo;
import org.nutz.resource.NutResource;

/**
 * 封装了 jar 内的 Entity
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JarEntryResource extends NutResource {

	private JarFile jar;

	private JarEntry entry;

	public JarEntryResource(JarEntryInfo jeInfo) throws IOException {
		this.jar = new JarFile(jeInfo.getJarPath());
		this.entry = jar.getJarEntry(jeInfo.getEntryName());
		if (null == this.entry)
			throw new IOException("Invalid JarEntry :" + jeInfo);
		this.name = jeInfo.getEntryName();
	}

	public JarEntryResource(JarFile jar, JarEntry jen, String name) {
		this.jar = jar;
		this.entry = jen;
		this.name = name;
	}

	public JarFile getJar() {
		return jar;
	}

	public JarEntry getEntry() {
		return entry;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return jar.getInputStream(entry);
	}

}
