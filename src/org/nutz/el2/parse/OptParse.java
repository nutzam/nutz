package org.nutz.el2.parse;

import java.util.Queue;

import org.nutz.el2.Opt.AndOpt;
import org.nutz.el2.Opt.DivOpt;
import org.nutz.el2.Opt.EQOpt;
import org.nutz.el2.Opt.GTEOpt;
import org.nutz.el2.Opt.GTOpt;
import org.nutz.el2.Opt.LBracketOpt;
import org.nutz.el2.Opt.LTEOpt;
import org.nutz.el2.Opt.LTOpt;
import org.nutz.el2.Opt.ModOpt;
import org.nutz.el2.Opt.MulOpt;
import org.nutz.el2.Opt.NEQOpt;
import org.nutz.el2.Opt.NotOpt;
import org.nutz.el2.Opt.OrOpt;
import org.nutz.el2.Opt.PlusOpt;
import org.nutz.el2.Opt.RBracketOpt;
import org.nutz.el2.Opt.SubOpt;

/**
 * 操作符转换器
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class OptParse implements Parse {

	public Object fetchItem(Queue<Character> exp){
		if(exp.isEmpty()){
			throw new RuntimeException();
		}
		switch(exp.peek()){
		case '+':
			exp.poll();
			return new PlusOpt();
		case '-':
			exp.poll();
			return new SubOpt();
		case '*':
			exp.poll();
			return new MulOpt();
		case '/':
			exp.poll();
			return new DivOpt();
		case '%':
			exp.poll();
			return new ModOpt();
		case '(':
			exp.poll();
			return new LBracketOpt();
		case ')':
			exp.poll();
			return new RBracketOpt();
		case '>':
			exp.poll();
			switch(exp.peek()){
			case '=':
				exp.poll();
				return new GTEOpt();
			}
			return new GTOpt();
		case '<':
			exp.poll();
			switch(exp.peek()){
			case '=':
				exp.poll();
				return new LTEOpt();
			}
			return new LTOpt();
		case '=':
			exp.poll();
			switch(exp.peek()){
			case '=':
				exp.poll();
				return new EQOpt();
			}
			throw new RuntimeException("表达式错误,请检查'='后是否有非法字符!");
		case '!':
			exp.poll();
			switch(exp.peek()){
			case '=':
				exp.poll();
				return new NEQOpt();
			}
			return new NotOpt();
		case '|':
			exp.poll();
			switch(exp.peek()){
			case '|':
				exp.poll();
				return new OrOpt();
			}
			throw new RuntimeException("表达式错误,请检查'|'后是否有非法字符!");
		case '&':
			exp.poll();
			switch(exp.peek()){
			case '&':
				exp.poll();
				return new AndOpt();
			}
			throw new RuntimeException("表达式错误,请检查'|'后是否有非法字符!");
		case '.':
			exp.poll();
			return
		}
		return null;
	}

}
