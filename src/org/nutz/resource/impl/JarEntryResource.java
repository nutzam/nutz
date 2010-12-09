package org.nutz.resource.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.nutz.lang.Lang;
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
		List<JarEntry> ens = Lang.enum2collection(jar.entries(), new ArrayList<JarEntry>());
		for (JarEntry en : ens)
			System.out.println(en.getName());
		this.entry = jar.getJarEntry(jeInfo.getEntryName());
		if (null == this.entry)
			throw new IOException("Invalid JarEntry :" + jeInfo);
		this.name = jeInfo.getEntryName();
	}

	public JarEntryResource(JarFile jar, JarEntry jen) {
		this.jar = jar;
		this.entry = jen;
		this.name = jen.getName();
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
