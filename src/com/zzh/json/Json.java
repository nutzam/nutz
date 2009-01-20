package com.zzh.json;

import java.io.InputStream;

import com.zzh.lang.types.Castors;

public class Json {

	public static Object fromJson(InputStream ins) {
		return new JsonParsing(Castors.me()).parseFromJson(ins, null);
	}

	public static Object fromJson(InputStream ins, Castors castors) {
		return new JsonParsing(castors).parseFromJson(ins, null);
	}

	public static <T> T fromJson(InputStream ins, Class<T> type) {
		return new JsonParsing(Castors.me()).parseFromJson(ins, type);
	}

	public static <T> T fromJson(InputStream ins, Class<T> type, Castors castors) {
		return new JsonParsing(castors).parseFromJson(ins, type);
	}

	public static String toJson(Object obj) {
		return (new JsonRendering(null, Castors.me())).convert(obj).toString();
	}

	public static String toJson(Object obj, JsonFormat format) {
		return (new JsonRendering(format, Castors.me())).convert(obj).toString();
	}

	public static String toJson(Object obj, JsonFormat format, Castors castors) {
		return (new JsonRendering(format, castors)).convert(obj).toString();
	}

}
