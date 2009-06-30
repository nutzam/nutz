package org.nutz.log;

import static org.junit.Assert.*;
import static java.lang.System.*;

import java.sql.Timestamp;

import org.junit.Test;
import org.nutz.dao.test.meta.Pojos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.json.JsonLoader;

public class LogTest {

	public static void main(String[] args) {
		Ioc ioc = new NutIoc(
				new JsonLoader("org/nutz/log/log.js", "org/nutz/dao/test/meta/pojo.js"));
		StringBuilder sb = ioc.get(StringBuilder.class, "sb");
		Pojos pojos = ioc.get(Pojos.class, "metas");
		pojos.init();
		out.println(sb);
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

	@Test
	public void test_normal_logoutput() {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		String ss = "ABC";
		String ts = time.toString();
		long tms = time.getTime();
		String exp = ">DemoService.voidMethod(zzh)(" + ts + ")\n";
		exp += "~DemoService.voidMethod:void\n";
		exp += ">DemoService.reIntMethod(ABC)\n";
		exp += "~DemoService.reIntMethod:3\n";
		exp += ">DemoService.reTimestampMethod(" + tms + ")\n";
		exp += "~DemoService.reTimestampMethod:" + ts + "\n";
		exp += ">DemoService.reStringMethod(ABC)(" + tms + ")\n";
		exp += ">>>DemoService.reIntMethod(ABC)\n";
		exp += "~~~DemoService.reIntMethod:3\n";
		exp += ">>>DemoService.reTimestampMethod(" + tms + ")\n";
		exp += "~~~DemoService.reTimestampMethod:" + ts + "\n";
		exp += "~DemoService.reStringMethod:" + ts + ":3\n";
		exp += ">DemoService.reNullObjectMethod(ABC)\n";
		exp += "~DemoService.reNullObjectMethod:null\n";

		Ioc ioc = new NutIoc(new JsonLoader("org/nutz/log/log.js"));
		StringBuilder sb = ioc.get(StringBuilder.class, "sb");
		DemoService demo = ioc.get(DemoService.class, "srv");
		assertEquals(ss, demo.abc());
		demo.voidMethod("zzh", time);
		demo.reIntMethod(ss);
		demo.reTimestampMethod(time.getTime());
		demo.reStringMethod(ss, time.getTime());
		demo.reNullObjectMethod(ss);
		assertEquals(exp, sb.toString());
	}

}
