package com.zzh.mvc.localize;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;

public class ListLocalization implements Controllor {

	private String[] list;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return list;
	}

}
