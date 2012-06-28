package org.nutz.dao.entity.annotation;

public @interface Index {

    boolean unique() default true;

    String name();

    String[] fields();

}
