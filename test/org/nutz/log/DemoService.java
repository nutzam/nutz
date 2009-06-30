package org.nutz.log;

import java.sql.Timestamp;

public class DemoService {

	public String abc() {
		return "ABC";
	}

	public void voidMethod(String s, Timestamp time) {}

	public int reIntMethod(String s) {
		if (null == s)
			return 0;
		return s.length();
	}

	public Timestamp reTimestampMethod(long ms) {
		return new Timestamp(ms);
	}

	public String reStringMethod(String s, long ms) {
		int i = reIntMethod(s);
		Timestamp t = reTimestampMethod(ms);
		return t.toString() + ":" + i;
	}

	public Object reNullObjectMethod(String t) {
		return null;
	}

}
