package org.nutz.resource.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.util.Disks;
import org.nutz.resource.NutResource;

/**
 * 记录了一个磁盘文件资源
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FileResource extends NutResource {

	private File file;

	public FileResource(File base, File file) {
		this(base.getAbsolutePath(), file);
	}

	public FileResource(String base, File file) {
		base = Disks.getCanonicalPath(base);
		this.name = Disks.getCanonicalPath(file.getAbsolutePath());
		if (this.name.startsWith(base))
			this.name = this.name.substring(base.length() + 1);
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
}
