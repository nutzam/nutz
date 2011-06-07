package org.nutz.el.val;

import java.math.BigDecimal;

import org.nutz.el.El;
import org.nutz.el.ElException;
import org.nutz.el.ElValue;

public class NumberElValue implements ElValue {

	private BigDecimal val;
	
	public NumberElValue(BigDecimal val) {
		this.val = val;
	}
	
	public NumberElValue(Float val) {
		this.val = new BigDecimal(val.toString());
	}
	
	public NumberElValue(Long val) {
		this.val = new BigDecimal(val.toString());
	}
	
	public NumberElValue(Integer val) {
		this.val = new BigDecimal(val.toString());
	}
	
	public ElValue invoke(ElValue[] args) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "invoke");
	}

	public ElValue getProperty(ElValue val) {
		throw new ElException("%s don't support [%s]!", getClass().getSimpleName(), "getProperty");
	}

	public Object get() {
		return val;
	}

	public Boolean getBoolean() {
		return val.intValue() != 0;
	}

	public Integer getInteger() {
		return val.intValue();
	}

	public Float getFloat() {
		return val.floatValue();
	}

	public Long getLong() {
		return val.longValue();
	}

	public String getString() {
		return val.toString();
	}

	public ElValue plus(ElValue ta) {
		BigDecimal result = new BigDecimal(val.toString()).add(new BigDecimal(ta.toString()));
		return new NumberElValue(result);
	}

	public ElValue sub(ElValue ta) {
		BigDecimal result = new BigDecimal(val.toString()).subtract(new BigDecimal(ta.toString()));
		return new NumberElValue(result);
	}

	public ElValue mul(ElValue ta) {
		BigDecimal result = new BigDecimal(val.toString()).multiply(new BigDecimal(ta.toString()));
		return new NumberElValue(result);
	}

	public ElValue div(ElValue ta) {
		BigDecimal result = null;
		
		try {
			result = new BigDecimal(val.toString()).divide(new BigDecimal(ta.toString()));
		}
		// 无法表示准确的商值的时候取整数
		catch (ArithmeticException e) {
			result = new BigDecimal(val.toString()).divide(new BigDecimal(ta.toString()), 0, BigDecimal.ROUND_DOWN);
		}
		
		return new NumberElValue(result);
	}

	public ElValue mod(ElValue ta) {
		BigDecimal result = new BigDecimal(val.toString()).remainder(new BigDecimal(ta.toString()));
		return new NumberElValue(result);
	}

	public ElValue isEquals(ElValue ta) {
		return val.equals(ta.get()) ? El.TRUE : El.FALSE;
	}

	public ElValue isNEQ(ElValue ta) {
		return !val.equals(ta.get()) ? El.TRUE : El.FALSE;
	}

	public ElValue isGT(ElValue ta) {
		return val.compareTo((BigDecimal) ta.get()) > 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isLT(ElValue ta) {
		return val.compareTo((BigDecimal) ta.get()) < 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isGTE(ElValue ta) {
		return val.compareTo((BigDecimal) ta.get()) >= 0 ? El.TRUE : El.FALSE;
	}

	public ElValue isLTE(ElValue ta) {
		return val.compareTo((BigDecimal) ta.get()) <= 0 ? El.TRUE : El.FALSE;
	}

	public String toString() {
		return val.toString();
	}

}
