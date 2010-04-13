package org.nutz.mock.servlet.multipart.inputing;

import java.io.File;
import java.io.FileNotFoundException;

import org.nutz.lang.Lang;

public abstract class Inputings {

	public static Inputing wrap(String fmt, Object... args) {
		return new StringInputing(String.format(fmt, args) + "\r\n");
	}

	public static Inputing name(String name) {
		return wrap("Content-Disposition: form-data; name=\"%s\"", name);
	}

	public static Inputing fileName(String name, String fileName) {
		return wrap("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", name, fileName);
	}

	public static Inputing contentType(String contentType) {
		return wrap("Content-Type: %s", contentType);
	}

	public static Inputing blankLine() {
		return new StringInputing("\r\n");
	}

	public static Inputing data(String str) {
		return wrap(str);
	}

	public static Inputing file(File f) {
		try {
			return new FileInputing(f);
		}
		catch (FileNotFoundException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
