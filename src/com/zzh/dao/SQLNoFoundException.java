package com.zzh.dao;

public class SQLNoFoundException extends RuntimeException {

	private static final long serialVersionUID = -3449985653479894065L;

	public SQLNoFoundException(String message) {
		super(message);
	}

}
