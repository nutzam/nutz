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

	private JarFile jar;

	private JarEntry entry;

	public JarEntryResource(JarFile jar, JarEntry jen) {
		this.jar = jar;
		this.entry = jen;
		this.name = jen.getName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return jar.getInputStream(entry);
	}

}
