package com.zzh.lang;

import com.zzh.trans.Atom;

public class Stopwatch {

	public static Stopwatch test(Atom atom){
		Stopwatch sw = new Stopwatch();
		sw.start();
		atom.run();
		sw.stop();
		return sw;
	}

	public static void printTest(Atom atom){
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

	public long getTimeInMillis() {
		return to - from;
	}

	@Override
	public String toString() {
		return String.format("Total: %dms : [%s]=>[%s]", this.getTimeInMillis(), new java.sql.Time(
				from).toString(), new java.sql.Time(to).toString());
	}

}
