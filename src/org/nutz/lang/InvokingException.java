package org.nutz.lang;

import static java.lang.String.format;

@SuppressWarnings("serial")
public class InvokingException extends RuntimeException {

    public InvokingException(String format, Object... args) {
        super(format(format, args));
    }

    public InvokingException(String msg, Throwable cause) {
        super(String.format(msg, cause.getMessage()), cause);
    }
}
