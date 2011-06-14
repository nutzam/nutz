package org.nutz.el2.parse;

import java.util.Queue;

import org.nutz.el2.opt.arithmetic.DivOpt;
import org.nutz.el2.opt.arithmetic.LBracketOpt;
import org.nutz.el2.opt.arithmetic.ModOpt;
import org.nutz.el2.opt.arithmetic.MulOpt;
import org.nutz.el2.opt.arithmetic.PlusOpt;
import org.nutz.el2.opt.arithmetic.RBracketOpt;
import org.nutz.el2.opt.arithmetic.SubOpt;
import org.nutz.el2.opt.logic.AndOpt;
import org.nutz.el2.opt.logic.EQOpt;
import org.nutz.el2.opt.logic.GTEOpt;
import org.nutz.el2.opt.logic.GTOpt;
import org.nutz.el2.opt.logic.LTEOpt;
import org.nutz.el2.opt.logic.LTOpt;
import org.nutz.el2.opt.logic.NEQOpt;
import org.nutz.el2.opt.logic.NotOpt;
import org.nutz.el2.opt.logic.OrOpt;
import org.nutz.el2.opt.logic.QuestionOpt;
import org.nutz.el2.opt.logic.QuestionSelectOpt;
import org.nutz.el2.opt.object.AccessOpt;
import org.nutz.el2.opt.object.ArrayOpt;
import org.nutz.el2.opt.object.CommaOpt;

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
		case '?':
			exp.poll();
			return new QuestionOpt();
		case ':':
			exp.poll();
			return new QuestionSelectOpt();
		
		case '.':
			exp.poll();
			return new AccessOpt();
		case ',':
			exp.poll();
			return new CommaOpt();
		case '[':
			exp.poll();
			return new CommaOpt();
//			return new ListOpt();
		case ']':
			exp.poll();
			return new ArrayOpt();
		}
		return null;
	}

}
