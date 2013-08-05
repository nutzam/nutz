package org.nutz.lang.socket;

@SuppressWarnings("serial")
public class CloseSocketException extends RuntimeException {

    public CloseSocketException() {
        super();
    }

    public CloseSocketException(String msg) {
        super(msg);
    }
}
