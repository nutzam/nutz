package org.nutz.mvc.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.nutz.lang.Lang;

public class MultiFileContent implements MultiReadable {

	private String name;
	private File file;
	
	private int [] data;
	
	private int size;
	
	private int index = 0;

	public MultiFileContent(String name, File file, int buffer) {
		this.name = name;
		this.file = file;
		size = (int)file.length();
		data = new int [size];
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			for (int i = 0; i < data.length; i++) {
			data [i] = bis.read();
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public int read() throws Exception {
		if (index == size)
			return -1;
		return data[index++];
	}

	public long length() {
		return file.length();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void close() throws IOException {
		data = null;
	}

}
