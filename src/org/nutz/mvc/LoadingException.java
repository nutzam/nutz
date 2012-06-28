package org.nutz.mvc;

public class LoadingException extends RuntimeException {

    private static final long serialVersionUID = -943194644274145836L;

    public LoadingException(Throwable cause) {
        super(cause);
    }

    public LoadingException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }

}
