package org.nutz.log.helper.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class TestAppender implements Appender {

	public static LoggingEvent lastEvent;
	
	protected String name; 
	
	public void addFilter(Filter filter) {
		
	}

	public void doAppend(LoggingEvent event) {
		lastEvent = event;
	}

	public ErrorHandler getErrorHandler() {
		return null;
	}

	public Filter getFilter() {
		return null;
	}

	public Layout getLayout() {
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean requiresLayout() {
		return false;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		
	}

	public void setLayout(Layout layout) {
		
	}

	public void setName(String name) {
		this.name = name;
	}

	public static LoggingEvent getLastEvent() {
		return lastEvent;
	}

	public void clearFilters() {}

	public void close() {}

}
