package org.nutz.mvc.upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.nutz.lang.Streams;

public class MultiFileContent implements MultiReadable {

	private String name;
	private File file;
	private InputStream ins;

	public MultiFileContent(String name, File file) {
		this.name = name;
		this.file = file;
		ins = new BufferedInputStream(Streams.fileIn(file));
	}

	public int read() throws Exception {
		return ins.read();
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
		ins.close();
	}

}
