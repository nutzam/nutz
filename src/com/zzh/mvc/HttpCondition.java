package com.zzh.mvc;

import javax.servlet.http.HttpServletRequest;

import com.zzh.dao.Condition;

public interface HttpCondition extends Condition {

	void valueOf(HttpServletRequest request);

}
