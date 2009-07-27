package org.nutz.log;

import java.io.IOException;
import java.util.Map;

public class ConsoleOutput implements LogOutput {

	@Override
	public void output(String str) throws IOException {
		System.out.print(str);
	}

	@Override
	public void setup(Map<String, Object> conf) {}

}
