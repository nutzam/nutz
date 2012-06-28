package org.nutz.mvc;

public class ObjectInfo<T> {

    private Class<T> type;

    private String[] args;

    public ObjectInfo(Class<T> type, String[] args) {
        this.type = type;
        this.args = args;
    }

    public Class<T> getType() {
        return type;
    }

    public String[] getArgs() {
        return args;
    }

}
