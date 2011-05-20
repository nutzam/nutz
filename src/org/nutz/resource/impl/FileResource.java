package org.nutz.resource.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Streams;
import org.nutz.lang.util.Disks;
import org.nutz.resource.NutResource;

/**
 * 记录了一个磁盘文件资源
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FileResource extends NutResource {

	private File file;

	public FileResource(File f) {
		this.file = f;
		this.name = f.getName();
	}

	public FileResource(File base, File file) {
		this(base.getAbsolutePath(), file);
	}

	public FileResource(String base, File file) {
		base = Disks.normalize(Disks.getCanonicalPath(base));
		if (!base.endsWith("/"))
			base += "/";
		this.name = Disks.normalize(Disks.getCanonicalPath(file.getAbsolutePath()));
		this.name = this.name.substring(this.name.indexOf(base) + base.length()).replace('\\', '/');
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public FileResource setFile(File file) {
		this.file = file;
		return this;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return Streams.fileIn(file);
	}

}
