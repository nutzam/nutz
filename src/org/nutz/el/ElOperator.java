package org.nutz.el;

import org.nutz.lang.util.Context;

/**
 * 表达式的操作符，实现这个接口的类，会被自动扫描
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ElOperator {

	/**
	 * 操作符执行。不同的操作符会调用 ElObj 以及 ElValue 的不同方法
	 * 
	 * @param context
	 *            上下文对象
	 * @param left
	 *            左对象
	 * @param right
	 *            右对象
	 * @return 结果值
	 */
	ElValue execute(Context context, ElObj left, ElObj right);

	void setWeight(int weight);

	void setString(String str);

	int getWeight();
	
	String getString();

	/**
	 * 同另外一个操作符比较权重
	 * 
	 * @param opt
	 *            另一个操作符
	 * @return true － 本身权重更高, false - 同权或权重更低
	 */
	boolean isHigherThan(ElOperator opt);

}
