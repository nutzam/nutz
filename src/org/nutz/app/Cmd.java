package org.nutz.app;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

import static java.lang.String.*;

public abstract class Cmd {

	protected Application app;

	public Cmd(Application app) {
		this.app = app;
	}

	public abstract void exec(CmdParams params) throws Exception;

	public String help() {
		String name = this.getClass().getName();
		File file = findHelp(name, "." + app.getLang() + ".txt");
		if (null == file) {
			file = findHelp(name, ".txt");
		}
		if (null == file || !file.exists() || !file.isFile())
			return format("Fail to find manual file for '%s'!", name);
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.dup('=', 100));
		sb.append(format("\n # Help File :: <%s>\n", name));
		sb.append(Strings.dup('~', 100));
		sb.append(format("\n%s\n", Lang.readAll(Streams.fileInr(file))));
		sb.append(Strings.dup('=', 100));
		return sb.toString();
	}

	private File findHelp(String name, String suffix) {
		String path = name.replace('.', '/');
		path += suffix;
		return Files.findFile(path);
	}

}
