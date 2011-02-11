package org.nutz.el.obj;

import org.nutz.el.ElObj;
import org.nutz.el.ElValue;
import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

/**
 * 条件表达式
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ConditionalElObj implements ElObj {

	private ElObj test;

	private ElObj trueObj;

	private ElObj falseObj;

	public ElValue eval(Context context) {
		if (test.eval(context).getBoolean())
			return trueObj.eval(context);
		return falseObj.eval(context);
	}

	public ElValue[] evalArray(Context context) {
		throw Lang.noImplement();
	}

	public ElObj getTest() {
		return test;
	}

	public void setTest(ElObj test) {
		this.test = test;
	}

	public ElObj getTrueObj() {
		return trueObj;
	}

	public void setTrueObj(ElObj trueObj) {
		this.trueObj = trueObj;
	}

	public ElObj getFalseObj() {
		return falseObj;
	}

	public void setFalseObj(ElObj falseObj) {
		this.falseObj = falseObj;
	}

}
