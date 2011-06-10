package org.nutz.el2.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * 转换器,也就是用来将字符串转换成队列.
 * @ TODO 这个类的名字不知道取什么好...
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Converter {
	private static final List<Parse> parses = new ArrayList<Parse>();
	static{
		parses.add(new OptParse());
		parses.add(new ObjParse());
		parses.add(new ValParse());
	}
	//表达式字符队列
	private Queue<Character> exp;
	
	public Converter(Queue<Character> expression) {
		this.exp = expression;
		skipSpace();
	}
	
	/**
	 * 取得第一项
	 * @param exp
	 * @return
	 * @throws IOException
	 */
	public Object readNext() throws IOException{
		Object obj = null;
		for(Parse parse : parses){
			obj = parse.fetchItem(exp);
			if(obj != null){
				skipSpace();
				return obj;
			}
		}
		throw new RuntimeException("无法解析!");
	}
	/**
	 * 跳过空格,并返回是否跳过空格(是否存在空格)
	 * @return
	 */
	public boolean skipSpace(){
		boolean space = false;
		while(!exp.isEmpty() && exp.peek().equals(' ')){
			space = true;
			exp.poll();
		}
		return space;
	}

}
