package org.nutz.ioc;

@SuppressWarnings("serial")
public class ObjectNotFoundException extends RuntimeException {

	public ObjectNotFoundException(String name) {
		super("Object '" + name + "' failed to found!!!");
	}

	public ObjectNotFoundException(String name, Throwable cause) {
		super("Object '" + name + "' failed to found!!!", cause);
	}

}
