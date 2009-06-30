package org.nutz.app;

public class QuitCmd extends Cmd {

	public QuitCmd(Application app) {
		super(app);
	}

	@Override
	public void exec(CmdParams params) throws Exception {
		throw new QuitApplication();
	}

}
