package com.zzh.dao;

public class Conditions {

	public static Condition format(String format, Object... args) {
		return new SimpleCondition(format, args);
	}

	public static Condition wrap(String str) {
		return new SimpleCondition((Object) str);
	}

}
