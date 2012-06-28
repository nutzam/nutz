package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.impl.NutActionChainMaker;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface ChainBy {

    Class<? extends ActionChainMaker> type() default NutActionChainMaker.class;

    String[] args();

}
