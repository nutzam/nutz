package org.nutz.el2.opt.object;

import java.util.Queue;

import org.nutz.el2.opt.AbstractOpt;


/**
 * 列表对象,基本上用来做为一个分割符
 * @author juqkai(juqkai@gmail.com)
 *
 */
public class ListOpt extends AbstractOpt {

	public int fetchPriority() {
		return -1;
	}
	public void wrap(Queue<Object> operand) {
	}
	public Object calculate() {
		return null;
	}
	public String fetchSelf() {
		return "list";
	}

}
