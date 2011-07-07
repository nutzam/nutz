package org.nutz.el.parse;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.nutz.el.ElException;
import org.nutz.el.Parse;
import org.nutz.el.opt.arithmetic.LBracketOpt;
import org.nutz.el.opt.arithmetic.NegativeOpt;
import org.nutz.el.opt.arithmetic.RBracketOpt;
import org.nutz.el.opt.arithmetic.SubOpt;
import org.nutz.el.opt.object.InvokeMethodOpt;
import org.nutz.el.opt.object.MethodOpt;
import org.nutz.el.obj.IdentifierObj;
import org.nutz.lang.Lang;

/**
 * 转换器,也就是用来将字符串转换成队列.
 * @ JKTODO 这个类的名字不知道取什么好...
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class Converter {
	private static final List<Parse> parses = new ArrayList<Parse>();
	
	//表达式字符队列
	private CharQueue exp;
	//表达式项
	private Queue<Object> itemCache;
	//括号栈
	private Deque<BracketType> bracket = new LinkedList<BracketType>();
	
	//上一个数据
	private Object prev = null;
	
	public Converter(CharQueue reader) {
		this.exp = reader;
		itemCache = new LinkedList<Object>();
		skipSpace();
		initParse();
	}
	public Converter(String val) {
		this(Lang.inr(val));
	}
	public Converter(Reader reader){
		this(new CharQueueDefault(reader));
	}
	/**
	 * 初始化解析器
	 */
	private void initParse(){
		parses.add(new OptParse());
		parses.add(new StringParse());
		parses.add(new IdentifierParse());
		parses.add(new ValParse());
	}
	/**
	 * 重新设置解析器
	 */
	public void setParse(List<Parse> val){
		parses.addAll(val);
	}

	/**
	 * 初始化EL项
	 */
	public void initItems(){
		while(!exp.isEmpty()){
			Object obj = parseItem();
			//处理数组的情况
			if(obj.getClass().isArray()){
				for(Object o : (Object[])obj){
					itemCache.add(o);
				}
				continue;
			}
			itemCache.add(obj);
		}
	}
	
	/**
	 * 解析数据
	 */
	private Object parseItem(){
		Object obj = null;
		for(Parse parse : parses){
			obj = parse.fetchItem(exp);
			if(obj != null){
				skipSpace();
				return parseItem(obj);
			}
		}
		throw new ElException("无法解析!");
	}
	
	/**
	 * 转换数据,主要是转换负号,方法执行
	 */
	private Object parseItem(Object item){
		//左括号
		if(item instanceof LBracketOpt){
			if(prev instanceof IdentifierObj){
				item = new Object[]{new MethodOpt(), new LBracketOpt()};
				bracket.addFirst(BracketType.Method);
			}else {
				bracket.addFirst(BracketType.Default);
			}
		}
		//右括号
		if(item instanceof RBracketOpt){
			switch(bracket.poll()){
			case Method:
				item = new Object[]{new RBracketOpt(),new InvokeMethodOpt()};
			}
		}
		//转换负号'-'
		if(item instanceof SubOpt && NegativeOpt.isNegetive(prev)){
			item = new NegativeOpt();
		}
		prev = item;
		return item;
	}
	
	/**
	 * 跳过空格,并返回是否跳过空格(是否存在空格)
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
	 * 取得一个有效数据
	 */
	public Object fetchItem(){
		return itemCache.poll();
	}
	
	
	/**
	 * 是否结束
	 */
	public boolean isEnd(){
		return itemCache.isEmpty();
	}
	
	
	/**
	 * 括号类型
	 *
	 */
	enum BracketType{
		/**
		 * 方法
		 */
		Method,
		/**
		 * 默认
		 */
		Default;
	}
}
