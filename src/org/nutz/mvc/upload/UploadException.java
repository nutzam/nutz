package org.nutz.mvc.upload;

@SuppressWarnings("serial")
public class UploadException extends Exception {

    public UploadException() {
        super();
    }

    public UploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadException(String message) {
        super(message);
    }

    public UploadException(Throwable cause) {
        super(cause);
    }

}
