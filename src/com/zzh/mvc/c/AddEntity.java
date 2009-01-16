package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.EntityService;


public class AddEntity<T> extends EntityControllor<T> {

	@Override
	public T execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		T obj = getEntityAsJson(request);
		
		return ((EntityService<T>) getService()).insert(obj);
	}

}
