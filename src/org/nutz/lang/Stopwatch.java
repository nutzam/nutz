package org.nutz.lang;

/**
 * 秒表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Stopwatch {

	private long from;
	private long to;

	public static Stopwatch begin() {
		Stopwatch sw = new Stopwatch();
		sw.start();
		return sw;
	}

	public static Stopwatch run(Runnable atom) {
		Stopwatch sw = begin();
		atom.run();
		sw.stop();
		return sw;
	}

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
