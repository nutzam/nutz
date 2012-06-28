package org.nutz.dao.entity.annotation;

/**
 * 描述一个数据库字段类型
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public enum ColType {

    /**
     * 不解释
     */
    CHAR,
    
    /**
     * 不解释
     */
    BOOLEAN,

    /**
     * 不解释
     */
    VARCHAR,

    /**
     * 长文本，对应 Clob
     */
    TEXT,

    /**
     * 二进制，对应 Blob
     */
    BINARY,

    /**
     * 不解释
     */
    TIMESTAMP,

    /**
     * 不解释
     */
    DATETIME,

    /**
     * 不解释
     */
    DATE,

    /**
     * 不解释
     */
    TIME,

    /**
     * 整型:根据字段的宽度来决定具体的数据库字段类型
     */
    INT,

    /**
     * 浮点:根据字段的宽度和精度来决定具体的数据库字段类型
     */
    FLOAT
}
