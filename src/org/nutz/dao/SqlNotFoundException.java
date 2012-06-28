package org.nutz.dao;

@SuppressWarnings("serial")
public class SqlNotFoundException extends RuntimeException {

    public SqlNotFoundException(String key) {
        super(String.format("fail to find SQL '%s'!", key));
    }

}
