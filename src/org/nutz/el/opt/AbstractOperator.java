package org.nutz.el.opt;

import org.nutz.el.ElOperator;

public abstract class AbstractOperator implements ElOperator {

	protected int weight;

	protected boolean higherIfSame;

	protected String str;

	public boolean isHigherIfSame() {
		return higherIfSame;
	}

	public void setHigherIfSame(boolean higherIfSame) {
		this.higherIfSame = higherIfSame;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}

	public void setString(String str) {
		this.str = str;
	}

	public String getString() {
		return this.str;
	}

	public boolean isHigherThan(ElOperator opt) {
		if (isSameType(opt) && higherIfSame)
			return weight >= opt.getWeight();
		return weight > opt.getWeight();
	}

	public boolean isSameType(ElOperator opt) {
		return str.equals(opt.getString());
	}

	public boolean is(String optStr) {
		return null == str ? false : str.equals(optStr);
	}

	public String toString() {
		return str;
	}

}
