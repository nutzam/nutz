package org.nutz;

public class NutException extends Exception {

    private static final long serialVersionUID = 3258651706795084595L;

    public NutException(String message, Throwable cause) {
        super(message, cause);
    }

    public NutException(String message) {
        super(message);
    }

    public NutException(Throwable cause) {
        super(cause);
    }

    public NutException() {}
}
