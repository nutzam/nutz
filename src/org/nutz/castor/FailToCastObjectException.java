package org.nutz.castor;

@SuppressWarnings("serial")
public class FailToCastObjectException extends RuntimeException {

    public FailToCastObjectException(Throwable cause, String fmt, Object... args) {
        super(String.format(fmt, args), cause);
    }

    public FailToCastObjectException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }

    public FailToCastObjectException(String message) {
        super(message);
    }

    public FailToCastObjectException(String message, Throwable cause) {
        super(message, cause);
    }

}
