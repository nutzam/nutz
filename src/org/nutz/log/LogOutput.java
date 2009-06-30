package org.nutz.log;

import java.io.IOException;

public interface LogOutput {

	void output(String str) throws IOException;

}
