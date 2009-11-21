package org.nutz.log.helper.jdkLogger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class TestHandler extends Handler {

	public static LogRecord lastLogRecord = null;
	
	@Override
	public void close() throws SecurityException {}

	@Override
	public void flush() {}

	@Override
	public void publish(LogRecord logRecord) {
		lastLogRecord = logRecord;
	}

	@Override
	public synchronized Level getLevel() {
		return super.getLevel();
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		return super.isLoggable(record);
	}

	@Override
	public synchronized void setLevel(Level newLevel) throws SecurityException {
		super.setLevel(newLevel);
	}

}
