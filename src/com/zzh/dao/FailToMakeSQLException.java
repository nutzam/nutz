package com.zzh.dao;

public class FailToMakeSQLException extends RuntimeException {

	private static final long serialVersionUID = -6106444392847052115L;

	public FailToMakeSQLException(String message) {
		super(message);
	}

	public FailToMakeSQLException(Throwable cause) {
		super(cause);
	}

}
