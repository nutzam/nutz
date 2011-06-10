package org.nutz.el2.parse;

import java.util.Queue;

public class ObjParse implements Parse {
	public Object fetchItem(Queue<Character> exp) {
		
		switch(exp.peek()){
		case '\'':
		case '"':
			StringBuilder sb = new StringBuilder();
			exp.poll();
			while(!exp.isEmpty() && !(exp.peek().equals('\'') || exp.peek().equals('"'))){
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
