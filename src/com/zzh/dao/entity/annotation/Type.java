package com.zzh.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface Type {
	public static enum DEF {
		AUTO, INT, CHAR
	}

	DEF value() default DEF.AUTO;
}
