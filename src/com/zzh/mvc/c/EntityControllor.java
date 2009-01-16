package com.zzh.mvc.c;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.zzh.mvc.MvcUtils;
import com.zzh.service.EntityService;

public abstract class EntityControllor<T> extends AbstractControllor<EntityService<T>> {

	protected T getEntityAsJson(HttpServletRequest request) throws IOException {
		T obj = MvcUtils
				.getObjectAsJson(getService().getEntityClass(), request);
		return obj;
	}

}
