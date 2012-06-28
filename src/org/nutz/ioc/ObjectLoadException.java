package org.nutz.ioc;

public class ObjectLoadException extends Exception {

    private static final long serialVersionUID = -3120995514020314424L;

    public ObjectLoadException(String message) {
        this(message, null);
    }

    public ObjectLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
