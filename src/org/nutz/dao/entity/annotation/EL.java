package org.nutz.dao.entity.annotation;

import org.nutz.dao.DB;

public @interface EL {

    DB db() default DB.OTHER;

    String value();
    
}
