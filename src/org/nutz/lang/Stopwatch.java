package org.nutz.lang;

import org.nutz.trans.Atom;

public class Stopwatch {

	public static Stopwatch test(Atom atom) {
		Stopwatch sw = new Stopwatch();
		sw.start();
		atom.run();
		sw.stop();
		return sw;
	}

	public static void printTest(Atom atom) {
		System.out.println(test(atom).toString());
	}
	
	private long from;
	private long to;

	public long start() {
		from = System.currentTimeMillis();
		return from;
	}

	public long stop() {
		to = System.currentTimeMillis();
		return to;
	}

	public long getDuration() {
		return to - from;
	}

	public long getStartTime() {
		return from;
	}

	public long getEndTime() {
		return to;
	}

	@Override
	public String toString() {
		return String.format("Total: %dms : [%s]=>[%s]", this.getDuration(),
				new java.sql.Timestamp(from).toString(), new java.sql.Timestamp(to).toString());
	}

}
