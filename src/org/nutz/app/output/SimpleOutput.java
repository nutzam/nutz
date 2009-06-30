package org.nutz.app.output;

import org.nutz.app.Output;

public class SimpleOutput implements Output {

	@Override
	public void write(CharSequence s) {
		System.out.print(s);
	}

	@Override
	public void writeClientInput(CharSequence s) {}

}
