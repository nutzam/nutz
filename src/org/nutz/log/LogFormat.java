package org.nutz.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFormat {

	public static LogFormat create() {
		return create(true, "yy-MM-dd hh-mm-ss.SSS");
	}

	public static LogFormat create(boolean showThread, String timePattern) {
		return new LogFormat(showThread, timePattern);
	}

	private boolean showThread;
	private String pattern;
	private DateFormat df;
	private int width;

	private LogFormat(boolean showThread, String pattern) {
		this.showThread = true;
		this.pattern = pattern;
	}

	public String format(String fmt, Object... args) {
		String msg = String.format(fmt, args);
		if (showThread)
			msg = "[" + Thread.currentThread().getName() + "] " + msg;
		if (null != getDateFormat())
			msg = df.format(new Date(System.currentTimeMillis())) + " " + msg;
		return msg + "\n";
	}

	public int getWidth() {
		return width;
	}

	public LogFormat setWidth(int width) {
		this.width = width;
		return this;
	}

	public DateFormat getDateFormat() {
		if (null != df)
			return df;
		if (null != pattern)
			df = new SimpleDateFormat(pattern);
		return df;
	}

	public LogFormat setShowThread(boolean showThread) {
		this.showThread = showThread;
		return this;
	}

	public boolean isShowThread() {
		return showThread;
	}

}
