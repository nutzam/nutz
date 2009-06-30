package org.nutz.castor.castor;

import java.text.DateFormat;

import org.nutz.castor.Castor;

public abstract class DateTimeCastor<FROM, TO> extends Castor<FROM, TO> {

	protected DateFormat dateTimeFormat;
	protected DateFormat dateFormat;
	protected DateFormat timeFormat;

	public void setDateTimeFormat(DateFormat format) {
		this.dateTimeFormat = format;
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setTimeFormat(DateFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

}
