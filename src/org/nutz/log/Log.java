package org.nutz.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Log {

	public static final int OFF = 0;
	public static final int ERROR = 1;
	public static final int WARN = 1 << 1;
	public static final int INFO = 1 << 2;
	public static final int DEBUG = 1 << 3;

	private static final Map<String, Log> logs = new HashMap<String, Log>();

	public static Log get(String name) {
		return logs.get(name);
	}

	public static synchronized void addLog(String key, Log log) {
		logs.put(key, log);
	}

	private int level;
	private LogOutput output;
	private LogFormat format;

	public LogFormat getFormat() {
		return format;
	}

	public Log() {
		this(INFO, new ConsoleOutput(), LogFormat.create());
	}

	public Log(int level, LogOutput output, LogFormat format) {
		this.level = level;
		this.output = output;
		this.format = format;
	}

	public boolean can(int level) {
		return level >= this.level;
	}

	public void printlnf(String fmt, Object... args) {
		try {
			output.output(format.format(fmt, args));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void error(String fmt, Object... args) {
		if (can(ERROR))
			this.printlnf(fmt, args);
	}

	public void warn(String fmt, Object... args) {
		if (can(WARN))
			this.printlnf(fmt, args);
	}

	public void info(String fmt, Object... args) {
		if (can(INFO))
			this.printlnf(fmt, args);
	}

	public void debug(String fmt, Object... args) {
		if (can(DEBUG))
			this.printlnf(fmt, args);
	}

}
