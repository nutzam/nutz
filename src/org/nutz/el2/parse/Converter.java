package org.nutz.el2.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.nutz.el2.Operator;
import org.nutz.el2.opt.arithmetic.LBracketOpt;
import org.nutz.el2.opt.arithmetic.NegativeOpt;
import org.nutz.el2.opt.arithmetic.RBracketOpt;
import org.nutz.el2.opt.arithmetic.SubOpt;
import org.nutz.el2.opt.object.InvokeMethodOpt;
import org.nutz.el2.opt.object.ListOpt;

/**
 * 转换器,也就是用来将字符串转换成队列.
 * @ JKTODO 这个类的名字不知道取什么好...
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Converter {
	private static final List<Parse> parses = new ArrayList<Parse>();
	static{
		parses.add(new OptParse());
		parses.add(new StringParse());
		parses.add(new IdentifierParse());
		parses.add(new ValParse());
	}
	//表达式字符队列
	private Queue<Character> exp;
	
	//上一个数据
	private Object prev = null;
	//是否有执行操作,判断方法体的括号
	private boolean isInvoke = false;
	
	public Converter(Queue<Character> expression) {
		this.exp = expression;
		skipSpace();
	}
	
	/**
	 * 取得一个有效数据
	 * @param exp
	 * @return
	 * @throws IOException
	 */
	public Object fetchItem() throws IOException{
		Object obj = null;
		for(Parse parse : parses){
			obj = parse.fetchItem(exp);
			if(obj != null){
				skipSpace();
				return fetchItem(obj);
			}
		}
		throw new RuntimeException("无法解析!");
	}
	
	
	/**
	 * 转换数据,主要是转换负号,方法执行
	 * @param item
	 * @return
	 * @throws IOException
	 */
	private Object fetchItem(Object item) throws IOException{
		//左括号
		if(item instanceof LBracketOpt && !(prev instanceof Operator)){
			item = new ListOpt();
			isInvoke = true;
		}
		//右括号
		if(isInvoke && item instanceof RBracketOpt){
			item = new InvokeMethodOpt();
			isInvoke = false;
		}
		//转换负号'-'
		if(item instanceof SubOpt && (prev == null || prev instanceof Operator)){
			item = new NegativeOpt();
		}
		prev = item;
		return item;
	}
	
	
	/**
	 * 跳过空格,并返回是否跳过空格(是否存在空格)
	 * @return
	 */
	private boolean skipSpace(){
		boolean space = false;
		while(!exp.isEmpty() && Character.isWhitespace(exp.peek())){
			space = true;
			exp.poll();
		}
		return space;
	}
	/**
	 * 是否结束
	 * @return
	 */
	public boolean isEnd(){
		return exp.isEmpty();
	}
}
