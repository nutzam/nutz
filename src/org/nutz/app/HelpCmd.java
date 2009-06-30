package org.nutz.app;

import java.util.Arrays;

import org.nutz.lang.Strings;

public class HelpCmd extends Cmd {

	public HelpCmd(Application app) {
		super(app);
	}

	@Override
	public void exec(CmdParams params) throws Exception {
		String cmdName = params.one();
		if (Strings.isBlank(cmdName)) {
			// list all avaliable commands
			String[] cmds = app.cmds.keySet().toArray(new String[app.cmds.size()]);
			Arrays.sort(cmds);
			app.println();
			app.println("Command List:");
			app.print(Strings.dup('-', 96));
			for (int i = 0; i < cmds.length; i++) {
				if (i % 6 == 0)
					app.println();
				app.printf("%15s ", cmds[i]);
			}
			app.println();
			app.println(Strings.dup('-', 96));
			app.printlnf("Totally %d commands", cmds.length);
			app.println();
			app.println("run '? [cmdName]' to check ech command usage");
			app.println();
			return;
		}
		Cmd cmd = app.getCommand(cmdName);
		if (null == cmd) {
			app.printlnf("Can not find command '%s'", cmdName);
			return;
		}
		app.println(cmd.help());
	}

}
