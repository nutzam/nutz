package org.nutz.lang.util;

public class CursorException extends RuntimeException {

	private static final long serialVersionUID = 4852372895089148783L;

	CursorException(String action, int cursor, int size) {
		super(String.format("Error when %s: curor [%d] out of bytes[%d]", action, cursor, size));
	}

}
