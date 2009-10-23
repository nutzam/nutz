package org.nutz.dao.entity;

@SuppressWarnings("serial")
public class ErrorEntitySyntaxException extends RuntimeException {

	public ErrorEntitySyntaxException(Class<?> klass, String message) {
		super(String.format("Entity Error [%s] : %s", null == klass ? "NULL" : klass.getName(),
				message));
	}

	public ErrorEntitySyntaxException(String message) {
		super(message);
	}
	
	

}
