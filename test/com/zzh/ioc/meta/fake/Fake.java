package com.zzh.ioc.meta.fake;

public class Fake {

	private static int ID = 0;

	public static int getId() {
		return ++ID;
	}

}
