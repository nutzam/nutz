package org.nutz.json;

public @interface JsonIgnore {


    /**
     * @return 是否忽略整数(Mirror.isIntLike())字段的特殊值
     */
    int null_int() default -94518;

    /**
     * @return 是否忽略float或double字段的特殊值
     */
    double null_double() default -0.94518;
}
