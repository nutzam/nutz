package org.nutz.mvc2.upload;

import java.io.File;

public class TempFile {

	private File file;
	private FieldMeta meta;

	TempFile(FieldMeta meta, File f) {
		this.meta = meta;
		this.file = f;
	}

	public File getFile() {
		return file;
	}

	public FieldMeta getMeta() {
		return meta;
	}
}
