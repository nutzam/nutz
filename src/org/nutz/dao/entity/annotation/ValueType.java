package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.dao.entity.FieldValueType;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD })
public @interface ValueType {

	FieldValueType value() default FieldValueType.AUTO;
}
