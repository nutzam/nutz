package org.nutz.log;

import java.io.IOException;
import java.util.Map;

public interface LogOutput {

	void output(String str) throws IOException;
	
	void setup(Map<String,Object> conf);

}
