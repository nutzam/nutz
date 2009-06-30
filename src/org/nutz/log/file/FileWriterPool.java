package org.nutz.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import org.nutz.lang.Lang;

public class FileWriterPool {

	private static FileWriterPool pool = new FileWriterPool();

	public static FileWriterPool me() {
		return pool;
	}

	private HashMap<String, Writer> writers;

	public FileWriterPool() {
		writers = new HashMap<String, Writer>();
	}

	public Writer getWriter(File file) {
		Writer w = writers.get(file.getAbsolutePath());
		if (null == w)
			synchronized (this) {
				w = writers.get(file.getAbsolutePath());
				if (null == w) {
					try {
						w = new BufferedWriter(new FileWriter(file, true));
						writers.put(file.getAbsolutePath(), w);
					} catch (IOException e) {
						throw Lang.wrapThrow(e);
					}
				}
			}
		return w;
	}

	public void close(File file) {
		close(file.getAbsolutePath());
	}

	public synchronized void close(String path) {
		Writer w = writers.get(path);
		if (null != w)
			try {
				w.close();
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			} finally {
				writers.remove(path);
			}
	}

	public void close() {
		for (Writer w : writers.values()) {
			try {
				w.close();
			} catch (IOException e) {}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

}
