package org.nutz.log;

import java.io.IOException;

public class ConsoleOutput implements LogOutput {

	@Override
	public void output(String str) throws IOException {
		System.out.print(str);
	}

}
