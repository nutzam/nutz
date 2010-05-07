package org.nutz.ioc.loader.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.RUNTIME)
public @interface IocBean {

	/**
	 * 指定一个名字, 默认为取类名的首字母小写, 如 STing,会设置为sTing
	 */
	String name() default "";
	
	/**
	 * 每个单一的值,规则与 org.nutz.ioc.loader.annotation.Inject一致
	 * <p/> 这些值,对应构造方法的参数
	 * @see org.nutz.ioc.meta.IocValue
	 * @see org.nutz.ioc.loader.annotation.Inject
	 */
	String [] param() default {};
	
	String scope() default "app";
	
	boolean singleton() default true;
	
	String create() default "";
	
	String depose() default "";
	
	String fetch() default "";
	
//	/**
//	 * 还没设计
//	 */
//	@Deprecated
//	boolean auto() default false;
}
