package org.nutz.log.impl;

import org.nutz.log.Log;

public abstract class AbstractLog implements Log {

	protected boolean isErrorEnabled = true;
	protected boolean isWarnEnabled = true;
	protected boolean isInfoEnabled = false;
	protected boolean isDebugEnabled = false;
	protected boolean isTraceEnabled = false;

	protected static final int LEVEL_ERROR = 40;
	protected static final int LEVEL_WARN = 30;
	protected static final int LEVEL_INFO = 20;
	protected static final int LEVEL_DEBUG = 10;
	protected static final int LEVEL_TRACE = 0;

	protected abstract void log(int level, Object message, Throwable tx);

	protected void log(int level, LogInfo info) {
		log(level, info.message, info.tx);
	}

	public void error(Object... infos) {
		if (isErrorEnabled())
			log(LEVEL_ERROR, buildLogInfo(infos));
	}

	public void warn(Object... infos) {
		if (isWarnEnabled())
			log(LEVEL_WARN, buildLogInfo(infos));
	}

	public void info(Object... infos) {
		if (isInfoEnabled())
			log(LEVEL_INFO, buildLogInfo(infos));
	}

	public void debug(Object... infos) {
		if (isDebugEnabled())
			log(LEVEL_DEBUG, buildLogInfo(infos));
	}

	public void trace(Object... infos) {
		if (isTraceEnabled())
			log(LEVEL_TRACE, buildLogInfo(infos));
	}

	static final class LogInfo {
		String message;
		Throwable tx;

		static LogInfo NULL = new LogInfo();
		static LogInfo EMPTY = new LogInfo();
		static {
			NULL.message = "null";
			EMPTY.message = "";
		}
	}

	private static final LogInfo buildLogInfo(Object... infos) {
		if (infos == null)
			return LogInfo.NULL;
		if (infos.length == 0)
			return LogInfo.EMPTY;
		if (infos.length == 1) {
			Object obj = infos[0];
			if (obj == null)
				return LogInfo.NULL;
			LogInfo info = new LogInfo();
			if (obj instanceof Throwable) {
				info.tx = (Throwable) obj;
				info.message = info.tx.getMessage();
			} else
				info.message = String.valueOf(obj);
			return info;
		}
		Object obj = infos[0];
		LogInfo info = new LogInfo();
		if (null == obj) {
			info.message = "null";
		} else {
			char[] cs = String.valueOf(obj).toCharArray();
			int len = cs.length;
			int argIndex = 1;
			int argLen = infos.length;
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < len; i++) {
				char c = cs[i];
				switch (c) {
				case '%': {
					if (i + 1 < len && cs[i + 1] == 's') {
						if (argIndex < argLen) {
							sb.append(infos[argIndex]);
							argIndex++;
						}
						i++;
					} else
						sb.append(c);
					break;
				}
				case '\\': {
					sb.append(c);
					if (i + 1 < len) {
						sb.append(cs[i + 1]);
						i++;
					}
					break;
				}
				default:
					sb.append(c);
				}
			}
			info.message = sb.toString();
		}
		Object obj2 = infos[infos.length - 1];
		if (obj2 != null && obj2 instanceof Throwable)
			info.tx = (Throwable) obj2;
		return info;
	}

	public boolean isErrorEnabled() {
		return isErrorEnabled;
	}

	public boolean isWarnEnabled() {
		return isWarnEnabled;
	}

	public boolean isInfoEnabled() {
		return isInfoEnabled;
	}

	public boolean isDebugEnabled() {
		return isDebugEnabled;
	}

	public boolean isTraceEnabled() {
		return isTraceEnabled;
	}

	@Deprecated
	public void errorf(Object... infos) {
		error(infos);
	}

	@Deprecated
	public void warnf(Object... infos) {
		warn(infos);
	}

	@Deprecated
	public void infof(Object... infos) {
		info(infos);
	}

	@Deprecated
	public void debugf(Object... infos) {
		debug(infos);
	}

	@Deprecated
	public void tracef(Object... infos) {
		trace(infos);
	}
}
