package org.nutz.app.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;

public class RecordingOutput extends SimpleOutput {

	private File log;

	public RecordingOutput(String path) {
		if (null != path) {
			this.log = Files.findFile(path);
			if (null == log) {
				this.log = new File(path);
				try {
					Files.createNewFile(this.log);
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
	}

	@Override
	public void writeClientInput(CharSequence s) {
		if (null != log) {
			try {
				Writer w = new FileWriter(log, true);
				w.append(s).append("\n");
				w.flush();
				w.close();
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
