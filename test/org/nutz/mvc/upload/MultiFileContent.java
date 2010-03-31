package org.nutz.mvc.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MultiFileContent implements MultiReadable {

	private String name;
	private File file;
	private InputStream ins;

	public MultiFileContent(String name, File file) {
		this.name = name;
		this.file = file;
		try {
			ins = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int read() throws Exception {
		return ins.read();
	}

	@Override
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

	@Override
	public void close() throws IOException {
		ins.close();
	}

}
