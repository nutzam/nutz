package org.nutz.aop;

import java.lang.reflect.Method;

/**
 * 方法拦截器
 * <p>
 * 你可以通过实现接口的四个方法，在一个方法调用之前，之后，发生异常，发生错误时加入自己的额外逻辑
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public interface MethodInterceptor {

	/**
	 * 在被拦截方法调用之前，将调用该方法。 你可用通过这个方法的返回值，来控制是否真正的调用"被拦截方法"。
	 * 
	 * @param obj
	 *            被调用实例
	 * @param method
	 *            实例被调用方法
	 * @param args
	 *            被调用方法所需参数
	 * @return true，继续调用被拦截方法。false 将不会调用被拦截方法
	 */
	boolean beforeInvoke(Object obj, Method method, Object... args);

	/**
	 * 你可以通过这个函数，修改被拦截方法的返回值。默认的，你直接将 returnObj 返回即可
	 * 
	 * @param obj
	 *            被调用实例
	 * @param returnObj
	 *            实例被调用方法的返回
	 * @param method
	 *            实例被调用方法
	 * @param args
	 *            被调用方法所需参数
	 * @return 调用者真正将拿到的对象。 如果，你返回的对象类型是错误的，比如调用者希望得到一个 long， 但是，你拦截了这个方法，并返回一个
	 *         String，那么将发生一个类型转换的错误
	 */
	Object afterInvoke(Object obj, Object returnObj, Method method, Object... args);

	/**
	 * 当被拦截方法发生异常(Exception)，这个方法会被调用。
	 * 
	 * @param e
	 *            异常
	 * @param obj
	 *            被调用实例
	 * @param method
	 *            被调用方法
	 * @param args
	 *            被调用方法所需参数
	 * 
	 * @return 是否继续抛出异常
	 */
	boolean whenException(Exception e, Object obj, Method method, Object... args);

	/**
	 * 当被拦截方法发生错误(Error)，这个方法会被调用。
	 * 
	 * @param e
	 *            错误
	 * @param obj
	 *            被调用实例
	 * @param method
	 *            被调用方法
	 * @param args
	 *            被调用方法所需参数
	 * 
	 * @return 是否继续抛出错误
	 */
	boolean whenError(Throwable e, Object obj, Method method, Object... args);

}
