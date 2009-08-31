package org.nutz.app.output;

import org.nutz.app.Output;

public class SimpleOutput implements Output {

	public void write(CharSequence s) {
		System.out.print(s);
	}

	public void writeClientInput(CharSequence s) {}

}
