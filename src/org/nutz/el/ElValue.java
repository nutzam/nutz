package org.nutz.el;

/**
 * 表达式的计算结果
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ElValue {

	/**
	 * @return 真实的值
	 */
	Object get();

	/**
	 * @return 值的布尔形式
	 */
	Boolean getBoolean();

	/**
	 * @return 值的整数形式
	 */
	Integer getInteger();

	/**
	 * @return 值的浮点形式
	 */
	Float getFloat();
	
	/**
	 * @return 值的长整形式
	 */
	Long getLong();

	/**
	 * @return 值的字符串形式
	 */
	String getString();

	/**
	 * 调用自身的一个方法
	 * 
	 * @param args
	 *            调用路径，以及参数数组
	 * @return 调用结果
	 */
	ElValue invoke(ElValue[] args);

	/**
	 * 获取自身的一个属性
	 * 
	 * @param val
	 *            属性名
	 * @return 属性值
	 */
	ElValue getProperty(ElValue val);

	/**
	 * 加
	 * 
	 * @param ta
	 *            被操作项
	 * @return 计算结果
	 */
	ElValue plus(ElValue ta);

	/**
	 * 减
	 * 
	 * @param ta
	 *            被操作项
	 * @return 计算结果
	 */
	ElValue sub(ElValue ta);

	/**
	 * 乘
	 * 
	 * @param ta
	 *            被操作项
	 * @return 计算结果
	 */
	ElValue mul(ElValue ta);

	/**
	 * 除
	 * 
	 * @param ta
	 *            被操作项
	 * @return 计算结果
	 */
	ElValue div(ElValue ta);

	/**
	 * 取模
	 * 
	 * @param ta
	 *            被操作项
	 * @return 计算结果
	 */
	ElValue mod(ElValue ta);

	/**
	 * 判断相等
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isEquals(ElValue ta);
	
	/**
	 * 判断不相等
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isNEQ(ElValue ta);

	/**
	 * 判断是否大于
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isGT(ElValue ta);

	/**
	 * 判断是否小于
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isLT(ElValue ta);

	/**
	 * 判断是否大于等于
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isGTE(ElValue ta);

	/**
	 * 判断是否小于等于
	 * 
	 * @param ta
	 *            被操作项
	 * @return true 或 false
	 */
	ElValue isLTE(ElValue ta);

}
