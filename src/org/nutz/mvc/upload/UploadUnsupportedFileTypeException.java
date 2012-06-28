package org.nutz.mvc.upload;

@SuppressWarnings("serial")
public class UploadUnsupportedFileTypeException extends RuntimeException {

    public UploadUnsupportedFileTypeException(FieldMeta meta) {
        super(String.format("Unsupport file '%s' [%s] ",
                            meta.getFileLocalPath(),
                            meta.getContentType()));
    }

}
