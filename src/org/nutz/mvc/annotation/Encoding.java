package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了一个请求输入时候的 Charset (HttpServletRequest)，以及<br>
 * 输出的时候的 Charset (HttpServletResponse)
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see java.nio.charset.Charset
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Encoding {

    String input();

    String output();

}
