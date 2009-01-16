package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;

import com.zzh.lang.Lang;
import com.zzh.mvc.HttpCondition;
import com.zzh.mvc.SimpleHttpCondition;


public abstract class ConditionControllor<S> extends AbstractControllor<S> {

	private Class<? extends HttpCondition> conditionClass;

	void setHttpConditionProviderClass(Class<? extends HttpCondition> conditionProviderClass) {
		this.conditionClass = conditionProviderClass;
	}

	protected HttpCondition getCondition(HttpServletRequest request) {
		try {
			HttpCondition hcp = null;
			if (null == conditionClass) {
				hcp = new SimpleHttpCondition();
			} else {
				hcp = conditionClass.newInstance();
			}
			hcp.valueOf(request);
			return hcp;
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

}
