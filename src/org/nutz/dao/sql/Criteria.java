package org.nutz.dao.sql;

import org.nutz.dao.Condition;
import org.nutz.dao.util.cri.SqlExpressionGroup;

/**
 * 这个接口是对 Condition 接口进行扩充，主要为了能够更好的利用 PreparedStatement
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface Criteria extends Condition, PItem {

	SqlExpressionGroup where();
	
	OrderBy getOrderBy();

//
//	/**
//	 * @return 是否有有效的 Where 子句
//	 */
//	boolean hasWhere();
//
//	/**
//	 * 生成 PreparedStatement 语句片段。在语句中，如果要有参数，不能用 '?' 而必须用 '@xxx' 来声明。
//	 * <p>
//	 * 以便从 getWhereParamChain() 函数中获取对应的值链
//	 * 
//	 * @return 返回的代码片段模板或者是 null 或者是以 "WHERE" 开头（无空格）
//	 */
//	Segment getWhereStatement();
//
//	/**
//	 * 返回一个名值对。要同 getWhereStatement() 的返回对应。
//	 * 
//	 * @return WHERE 子句的参数
//	 */
//	Map<String, Object> getWhereParams();
//
//	/**
//	 * @return 是否有有效的 OrderBy 子句
//	 */
//	boolean hasOrderBy();
//
//	/**
//	 * 生成SQL语句
//	 * 
//	 * @return 返回的字符串或者是 null 或者是以 "ORDER BY" 开头（无空格）
//	 */
//	String getOrderByStatement();

	/*
	 * @return 是否有有效的 GROUP BY 子句
	 */
	// boolean hasGroupBy();

	/*
	 * 生成SQL语句
	 * 
	 * @return 返回的字符串或者是 null 或者是以 "GROUP BY" 开头（无空格）
	 */
	// String getGroupByStatement();

	/*
	 * @return 是否有有效的 HAVING 子句
	 */
	// boolean hasHaving();

	/*
	 * 生成 PreparedStatement 语句片段。在语句中，如果要有参数，不能用 '?' 而必须用 '@xxx' 来声明。 <p> 以便从
	 * getHavingParamChain() 函数中获取对应的值链
	 * 
	 * @return 返回的字符串或者是 null 或者是以 "HAVING" 开头（无空格）
	 */
	// String getHavingStatement();

	/*
	 * 返回一个名值对的值链。无所谓顺序，只要同 PreparedStatement 语句的参数名对应就好。
	 * 
	 * @return HAVING 子句的 PreparedStatement 值链
	 */
	// Chain getHavingParamChain();

}
