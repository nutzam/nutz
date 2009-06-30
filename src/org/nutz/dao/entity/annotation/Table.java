package org.nutz.dao.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.lang.Lang;

/**
 * Delete one entity table name. support dynamice string like:
 * 
 * <b>xxx${yy}xxx</b>
 * 
 * @author zozoh
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface Table {
	String value() default Lang.NULL;
}
