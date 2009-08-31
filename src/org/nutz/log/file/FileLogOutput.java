package org.nutz.log.file;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.log.LogOutput;

public class FileLogOutput implements LogOutput {

	private Writer writer;

	public FileLogOutput(String path) {
		this(new File(path));
	}

	public FileLogOutput(File file) {
		try {
			if (!file.exists())
				Files.createNewFile(file);
			writer = FileWriterPool.me().getWriter(file);
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void output(String str) throws IOException {
		writer.append(str);
		writer.flush();
	}

	public void setup(Map<String, Object> conf) {
	}

}
