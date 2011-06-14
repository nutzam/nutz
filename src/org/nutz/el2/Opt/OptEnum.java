package org.nutz.el2.opt;

/**
 * 操作符定义
 * @ JKTODO 感觉这个Enum是多余的...
 * @author juqkai(juqkai@gmail.com)
 *
 */
public enum OptEnum {
	/**
	 * 加
	 */
	PLUS, 
	/**
	 * 减
	 */
	SUB, 
	/**
	 * 乘
	 */
	MUL, 
	/**
	 * 除
	 */
	DIV,
	/**
	 * 求余
	 */
	MOD,
	
	/**
	 * 大于
	 */
	GT,
	/**
	 * 大于等于
	 */
	GTE,
	/**
	 * 小于
	 */
	LT,
	/**
	 * 小于等于
	 */
	LTE,
	/**
	 * 等于
	 */
	EQ,
	/**
	 * 不等于
	 */
	NEQ,
	/**
	 * 取反
	 */
	NOT,
	
	/**
	 * 左括号
	 */
	LBRACKET,
	/**
	 * 右括号
	 */
	RBRACKET, 
	/**
	 * 或
	 */
	OR, 
	/**
	 * 且
	 */
	AND, 
	/**
	 * ?
	 */
	QUESTION, 
	/**
	 * :
	 */
	QUESTION_SELECT, 
	/**
	 * .
	 */
	ACCESS, 
	/**
	 * invoke
	 */
	INVOKE, 
	/**
	 * 负号
	 */
	NEGATIVE, 
	/**
	 * ','
	 */
	COMMA,
	/**
	 * list
	 */
	LIST;
	
	public String toString(){
		switch(this){
		case PLUS :
			return "+";
		case SUB : 
			return "-";
		case MUL : 
			return "*";
		case DIV :
			return "/";
		case MOD :
			return "%";
		case GT :
			return ">";
		case GTE :
			return ">=";
		case LT :
			return "<";
		case LTE :
			return "<=";
		case EQ :
			return "==";
		case NEQ :
			return "!=";
		case NOT :
			return "!";
		case LBRACKET :
			return "(";
		case RBRACKET :
			return ")";
		case OR :
			return "||";
		case AND :
			return "&&";
		case QUESTION:
			return "?";
		case QUESTION_SELECT:
			return ":";
		case ACCESS:
			return ".";
		case INVOKE:
			return "invoke";
		case NEGATIVE:
			return "-";
		case COMMA:
			return ",";
		case LIST:
			return "list";
		}
		
		return "";
	}
}
