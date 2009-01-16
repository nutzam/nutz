package com.zzh.lang.types;

import java.text.SimpleDateFormat;

import com.zzh.lang.types.castor.DateTimeCastor;

public abstract class CastorSetting {

	private static int ID;

	private int id;

	protected CastorSetting() {
		id = ++ID;
	}

	public int getId() {
		return id;
	}

	protected void setup(Castor<?,?> c) {
		if (c instanceof DateTimeCastor) {
			((DateTimeCastor<?,?>) c).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
			((DateTimeCastor<?,?>) c).setTimeFormat(new SimpleDateFormat("HH:mm:ss"));
			((DateTimeCastor<?,?>) c).setDateTimeFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		}
	}

	protected String[] extraCastorPaths() {
		return null;
	}

}
