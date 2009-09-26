package org.nutz.lang;

import static java.lang.System.out;

import java.util.regex.Matcher;

public class Printer {

	public static void dump(Matcher m) {
		if (m.find())
			for (int i = 0; i <= m.groupCount(); i++)
				out.printf("%2d: %s\n", i, m.group(i));
		else
			out.println("No found!");
	}

}
