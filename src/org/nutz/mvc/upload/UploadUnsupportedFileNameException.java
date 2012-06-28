package org.nutz.mvc.upload;

@SuppressWarnings("serial")
public class UploadUnsupportedFileNameException extends RuntimeException {

    public UploadUnsupportedFileNameException(FieldMeta meta) {
        super(String.format("Unsupport file name '%s' ", meta.getFileLocalPath()));
    }

}
