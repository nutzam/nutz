package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.EntryDeterminer;
import org.nutz.mvc.impl.NutEntryDeterminer;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface Determiner {

    /**
     * 入口方法决断器
     */
    Class<? extends EntryDeterminer> value() default NutEntryDeterminer.class;

    /**
     * 构造参数,可以使用"ioc:xxxx"这种方式,从Ioc容器内获取, xxxx是IocBean在容器内的名称
     */
    String[] args() default {};

}
