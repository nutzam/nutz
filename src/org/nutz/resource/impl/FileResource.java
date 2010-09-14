package org.nutz.resource.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Lang;
import org.nutz.resource.NutResource;

/**
 * 记录了一个磁盘文件资源
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class FileResource extends NutResource {

	private File file;

	FileResource(String base, File file) {
		try {
			this.name = file.getCanonicalPath();
			if (this.name.startsWith(base))
				this.name = this.name.substring(base.length());
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
}
