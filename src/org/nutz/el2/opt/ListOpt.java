package org.nutz.el2.opt;

import java.util.Queue;


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
	public OptEnum fetchSelf() {
		return OptEnum.LIST;
	}

}
