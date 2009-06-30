package org.nutz.app;

import java.io.BufferedReader;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Files;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.trans.Atom;

public class BatchCmd extends Cmd {

	public BatchCmd(Application app) {
		super(app);
	}

	@Override
	public void exec(CmdParams params) throws Exception {
		String path = params.one();
		File file = null;
		if (!Strings.isBlank(path))
			file = Files.findFile(path);
		while (null == file || !file.isFile()) {
			if (null == file)
				app.said("File '%s' not exists!", path);
			else if (null != file && !file.isFile())
				app.said("Path '%s' should be a file!", path);
			path = app.asking("Tell me your batch file full path");
			file = Files.findFile(path);
		}
		final File script = file;
		app.said("OK, I will run the script '%s' for you ... ", file);
		app.println(Strings.dup('>', 80));
		app.printlnf(" <%s>", file);
		app.println(Strings.dup('-', 80));
		Stopwatch sw = Stopwatch.test(new Atom() {
			public void run() {
				try {
					List<String> cmdList = new LinkedList<String>();
					String line;
					BufferedReader reader = new BufferedReader(Streams.fileInr(script));
					while (null != (line = reader.readLine()))
						if (!Strings.isBlank(line))
							cmdList.add(line);
					reader.close();
					String[] cmds = cmdList.toArray(new String[cmdList.size()]);
					if (cmds.length > 0) {
						Application newapp = app.duplicate();
						newapp.setBatch(cmds);
						newapp.run();
					}
				} catch (QuitApplication e) {} catch (Throwable e) {
					app.said("Error happend!!!<%s> ", e.getMessage());
				}
			}
		});
		app.print("\n");
		app.println(Strings.dup('-', 80));
		app.printlnf(" End script : %s : ", sw, file);
		app.println(Strings.dup('<', 80));

	}

}
