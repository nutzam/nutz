package org.nutz.mvc.upload;

@SuppressWarnings("serial")
public class UploadInvalidFormatException extends UploadException {

    public UploadInvalidFormatException() {
        super();
    }

    public UploadInvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadInvalidFormatException(String message) {
        super(message);
    }

    public UploadInvalidFormatException(Throwable cause) {
        super(cause);
    }

}
