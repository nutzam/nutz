/**
 * 提供对 Java 类的拦截能力
 * <p>
 * 通过  MethodInterceptor 接口，对于 Java 类 public | protected 函数的提供了拦截能力。
 * 具体的做法是为被拦截类生成子类，并通过 ASM 生成字节码
 */
package org.nutz.aop;