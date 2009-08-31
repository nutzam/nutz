package org.nutz.log;

import java.io.IOException;
import java.util.Map;

public class ConsoleOutput implements LogOutput {

	public void output(String str) throws IOException {
		System.out.print(str);
	}

	public void setup(Map<String, Object> conf) {
	}

}
