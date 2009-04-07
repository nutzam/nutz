package com.zzh.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zzh.lang.Lang;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Id {
	IdType value() default IdType.AUTO_INCREASE;

	String fetch() default Lang.NULL;
}
