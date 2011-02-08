package org.nutz.el;

import org.nutz.lang.util.Context;

/**
 * 表达式的数据项目，比如，它可以是
 * 
 * <ul>
 * <li>布尔
 * <li>变量
 * <li>数字
 * <li>字符串
 * <li>等等
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ElObj {

	/**
	 * @param context
	 *            上下文对象
	 * @return 该对象在运行时得到的静态值
	 */
	ElValue eval(Context context);

	/**
	 * @param context
	 *            上下文对象
	 * @return 该对象在运行时得到一组静态值
	 */
	ElValue[] evalArray(Context context);

}
