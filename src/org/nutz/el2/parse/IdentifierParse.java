package org.nutz.el2.parse;

import java.util.Queue;

import org.nutz.el2.obj.IdentifierObj;

/**
 * 标识符转换
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class IdentifierParse implements Parse{

	public Object fetchItem(Queue<Character> exp) {
		StringBuilder sb = new StringBuilder();
		if(Character.isJavaIdentifierStart(exp.peek())){
			sb.append(exp.poll());
			while(!exp.isEmpty() && Character.isJavaIdentifierPart(exp.peek())){
				sb.append(exp.poll());
			}
			//@ JKTODO 这个地方可以弄个接口,然后整成自定义函数什么的
			if(sb.toString().equals("true")){
				return true;
			}
			if(sb.toString().equals("false")){
				return false;
			}
			return new IdentifierObj(sb.toString());
		}
		return null;
	}

}
