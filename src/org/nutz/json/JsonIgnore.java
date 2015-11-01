package org.nutz.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
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
