package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.EntityService;

public class Update<T> extends EntityControllor<T> {

	public Update(EntityService<T> service) {
		super(service);
	}

	public boolean ignoreNull;
	public String ignored;
	public String actived;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		T obj = this.getObject(request);
		if (null != ignored || null != actived)
			return service.dao().update(obj, ignored, actived);
		if (ignoreNull)
			return service.dao().update(obj, true);
		return service.update(obj);
	}

}
