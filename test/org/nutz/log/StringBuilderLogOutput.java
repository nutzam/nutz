package org.nutz.log;

import java.io.IOException;
import java.util.Map;

public class StringBuilderLogOutput implements LogOutput {

	private StringBuilder sb;

	public StringBuilderLogOutput(StringBuilder sb) {
		this.sb = sb;
	}

	public void output(String str) throws IOException {
		sb.append(str);
	}

	public void setup(Map<String, Object> conf) {}

}
