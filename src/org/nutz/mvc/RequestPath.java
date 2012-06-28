package org.nutz.mvc;

import org.nutz.lang.Strings;

public class RequestPath {

    private String url;

    private String path;

    private String suffix;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return Strings.isBlank(suffix) ? path : path + "." + suffix;
    }

}
