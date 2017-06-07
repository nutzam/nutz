package org.nutz.dao;

public interface Nesting {
	
	/**
	 * 构建一个嵌套sql语句
	 * @param names 嵌套查询的属性名
	 * @param classOfT 需要查询类
	 * @param cnd 嵌套查询条件
	 * @return 嵌套sql语句
	 */
	Nesting select(String names,Class<?>classOfT,Condition cnd);
}
