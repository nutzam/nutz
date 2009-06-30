package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.lang.Lang;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Column {

	String value() default Lang.NULL;

}
