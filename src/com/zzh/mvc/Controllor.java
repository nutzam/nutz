package com.zzh.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Controllor {

	Object execute(HttpServletRequest request, HttpServletResponse response) throws Throwable;

}
