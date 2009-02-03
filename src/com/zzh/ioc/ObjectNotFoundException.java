package com.zzh.ioc;

public class ObjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4572770186592275071L;

	public ObjectNotFoundException(String name) {
		super("Object '" + name + "' failed to found!!!");
	}

	public ObjectNotFoundException(String name, Throwable cause) {
		super("Object '" + name + "' failed to found!!!", cause);
	}

}
