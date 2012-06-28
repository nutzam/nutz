package org.nutz.mvc.upload;

@SuppressWarnings("serial")
public class UploadOutOfSizeException extends RuntimeException {

    public UploadOutOfSizeException(FieldMeta meta) {
        super(String.format("File '%s' out of size!", meta.getFileLocalPath()));
    }

}
