package org.nutz.mvc2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.mvc2.Setup;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
public @interface SetupBy {

	Class<? extends Setup> value();

}
