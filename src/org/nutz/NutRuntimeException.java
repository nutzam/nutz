package org.nutz;

public class NutRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -1381157074476752565L;

    public NutRuntimeException() {
        super();
    }

    public NutRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NutRuntimeException(String message) {
        super(message);
    }

    public NutRuntimeException(Throwable cause) {
        super(cause);
    }

}
