package org.nutz.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface NutTest {

	/**
	 * 测试方法完成后,是否回滚数据库操作
	 * <p/>请注意隐式事务提交,例如create table等操作无法进行回滚的
	 * @return true则回滚数据库操作
	 */
	boolean rollback() default false;
}
