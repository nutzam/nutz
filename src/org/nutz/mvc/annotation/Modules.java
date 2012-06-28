package org.nutz.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明了一个应用所有的模块
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal1985(wendal1985@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface Modules {

    /**
     * 每个模块一个类
     */
    Class<?>[] value() default {};
    
    /**
     * 需要扫描的package<p/>
     * <b>这个属性不受scanPackage的影响!!</b>
     */
    String[] packages() default {};

    /**
     * 是否搜索模块类同包以及子包的其他类
     */
    boolean scanPackage() default false;
}
