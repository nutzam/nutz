package com.zzh.mvc.upload;

import java.io.File;

import com.zzh.mvc.upload.Upload.FieldTitle;

public class UploadedFile {

	private File file;
	private FieldTitle title;

	public UploadedFile(FieldTitle title, File f) {
		this.title = title;
		this.file = f;
	}

	public File getFile() {
		return file;
	}

	public FieldTitle getTitle() {
		return title;
	}

}
