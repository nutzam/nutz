package org.nutz.el.speed;

import org.nutz.el.El;
import org.nutz.el.obj.BinElObj;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.trans.Atom;

/**
 * 一个基本的速度测试。 同 Java 代码相比，大约慢了 100－300倍 随着表达式的复杂，会更慢
 s* 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class SimpleSpeedTest {

	static int max = 50000;
	static int i = 0;

	public int abc(int i) {
		return i % 13;
	}

	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		final SimpleSpeedTest z = new SimpleSpeedTest();
		final BinElObj exp = El.compile("num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7)-z.abc(i)");
		final Context context = Lang.context("{num:0}");
		context.set("z", z);

		System.out.println("\n" + Strings.dup('=', 100));

		Stopwatch sw = Stopwatch.run(new Atom() {
			public void run() {
				int num = 0;
				for (int i = 0; i < max; i++)
					num = num + (i - 1 + 2 - 3 + 4 - 5 + 6 - 7) - z.abc(i);
				System.out.println("Num: " + num);
			}
		});

		System.out.println("\n" + Strings.dup('=', 100));

		Stopwatch sw2 = Stopwatch.run(new Atom() {
			public void run() {
				try {
					for (int i = 0; i < max; i++)
						context.set("num", exp.eval(context.set("i", i)).getInteger());
					System.out.println("Num: " + context.getInt("num"));
				}
				catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		});
		System.out.println("\n" + Strings.dup('=', 100));

		System.out.printf("\n%20s : %s", "Invoke", sw.toString());
		System.out.printf("\n%20s : %s", "Reflect", sw2.toString());

	}

}
