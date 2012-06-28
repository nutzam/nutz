package org.nutz.mvc.upload;

public class UploadStopException extends RuntimeException {

    private static final long serialVersionUID = -987980575457030395L;

    private UploadInfo info;

    public UploadStopException(UploadInfo info) {
        super(String.format("UploadStop: %d/%d", info.current, info.sum));
        this.info = info.clone();
        info.sum = -2;
        info.current = -2;
    }

    public UploadInfo getInfo() {
        return info;
    }

    public void setInfo(UploadInfo info) {
        this.info = info;
    }

}
