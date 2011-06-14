package org.nutz.el2.parse;

import java.util.Queue;

public class StringParse implements Parse {
	public Object fetchItem(Queue<Character> exp) {
		//@ JKTODO 添加转意字符
		switch(exp.peek()){
		case '\'':
		case '"':
			StringBuilder sb = new StringBuilder();
			char end = exp.poll();
			while(!exp.isEmpty() && !exp.peek().equals(end)){
				sb.append(exp.poll());
			}
			exp.poll();
			return sb.toString();
		}
		return null;
	}

	public static void main(String args[]){
		double x = .1 + 5;
		System.out.println(x);
	}
}
