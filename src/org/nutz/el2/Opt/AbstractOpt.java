package org.nutz.el2.Opt;

import org.nutz.el2.Operator;

public abstract class AbstractOpt implements Operator{
	/**
	 * 操作符对象自身的符号
	 * @return
	 */
	public abstract OptEnum fetchSelf();
	public boolean equals(Object obj) {
		if(obj.equals(fetchSelf())){
			return true;
		}
		return super.equals(obj);
	}
	public String toString() {
		return String.valueOf(fetchSelf());
	}

}
