package org.nutz.log.helper.log4j;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;

public class TestAppender extends ConsoleAppender {

	private static LoggingEvent lastEvent;

	public static LoggingEvent getLastEvent() {
		return lastEvent;
	}

	public void doAppend(LoggingEvent event) {
		lastEvent = event;
		super.doAppend(event);
	}

}
