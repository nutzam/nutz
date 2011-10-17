package org.nutz.test;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

/**
 * 本Runner旨在简化使用Nutz时的单元测试
 * <p/>当前支持的注解:
 * <p/>@NutTest(rollback=true) 当前仅有rollback属性,默认是false
 * <p/>查找顺序: 具体测试方法(不查找被override的父类方法),所在的类,递归查找父类
 * <p/>@IocBy @Iocbean @Inject 为测试了构建一个Ioc环境,语法与Mvc中@Iocby用法一致
 * <p/>查找顺序: 所在的类,递归查找父类. 当前类必须声明@Iocbean,其@Inject才会生效.
 * 即当前类必须使用注解定义为一个Iocbean
 * @author wendal
 *
 */
public class NutzJUnit4ClassRunner extends TestClassRunner {

	public NutzJUnit4ClassRunner(final Class<?> klass) throws InitializationError {
		super(klass, new NutTestClassMethodsRunner(klass));
	}
}
