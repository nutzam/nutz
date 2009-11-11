package org.nutz.log;

import static java.lang.System.*;

import org.nutz.dao.test.meta.Base;
import org.nutz.dao.test.meta.Pojos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;

public class LogTest {

	public static String getLogFilePath() {
		String path = System.getenv("NUTZ_TARGET_DIR");
		path += "/log.txt";
		return path;
	}

	public static void main(String[] args) {
		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/log/log2.js",
				"org/nutz/dao/test/meta/pojo.js"));
		Pojos pojos = ioc.get(Pojos.class, "metas");
		pojos.init();
		pojos.create4Platoon(Base.make("abc"), "rrr");
		out.println("Done!!!");
	}

	public static int getThreadDeep() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			if (stack[i].getClassName().equalsIgnoreCase(LogTest.class.getName()))
				return stack.length - i - 23;
		}
		return stack.length;
	}
}
