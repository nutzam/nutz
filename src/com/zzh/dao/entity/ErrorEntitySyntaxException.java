package com.zzh.dao.entity;

public class ErrorEntitySyntaxException extends RuntimeException {

	private static final long serialVersionUID = -7385165770496012406L;

	public ErrorEntitySyntaxException(Class<?> klass, String message) {
		super(String.format("Entity Error [%s] : %s", klass.getName(), message));
	}

}
