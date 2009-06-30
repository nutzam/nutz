package org.nutz.app;

@SuppressWarnings("serial")
public class StopCmdMessage extends RuntimeException {

	public StopCmdMessage() {
		super();
	}

	public StopCmdMessage(String message) {
		super(message);
	}

}
