package org.nutz.ioc;

public class IocException extends RuntimeException {

    private static final long serialVersionUID = -420118435729449317L;
    
    public IocException(Throwable cause) {
        super(cause);
    }
    
    public IocException(String fmt, Object... args) {
        super(String.format(fmt, args));
    }

    public IocException(Throwable cause, String fmt, Object... args) {
        super(String.format(fmt, args), cause);
    }

}
