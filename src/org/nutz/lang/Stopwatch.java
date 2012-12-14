package org.nutz.lang;

/**
 * 秒表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Stopwatch {

    private boolean nano;

    private long from;

    private long to;

    public static Stopwatch begin() {
        Stopwatch sw = new Stopwatch();
        sw.start();
        return sw;
    }

    public static Stopwatch beginNano() {
        Stopwatch sw = new Stopwatch();
        sw.nano = true;
        sw.start();
        return sw;
    }

    public static Stopwatch create() {
        return new Stopwatch();
    }

    public static Stopwatch createNano() {
        Stopwatch sw = new Stopwatch();
        sw.nano = true;
        return sw;
    }

    public static Stopwatch run(Runnable atom) {
        Stopwatch sw = begin();
        atom.run();
        sw.stop();
        return sw;
    }

    public static Stopwatch runNano(Runnable atom) {
        Stopwatch sw = beginNano();
        atom.run();
        sw.stop();
        return sw;
    }

    public long start() {
        from = currentTime();
        return from;
    }

    private long currentTime() {
        return nano ? System.nanoTime() : System.currentTimeMillis();
    }

    public long stop() {
        to = currentTime();
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
        return String.format(    "Total: %d%s : [%s]=>[%s]",
                                this.getDuration(),
                                (nano ? "ns" : "ms"),
                                new java.sql.Timestamp(from).toString(),
                                new java.sql.Timestamp(to).toString());
    }

}
