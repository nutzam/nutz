package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.dao.FieldFilter;
import com.zzh.lang.Strings;
import com.zzh.service.EntityService;
import com.zzh.trans.Atom;

public class Update<T> extends EntityControllor<T> {

	public Update(EntityService<T> service) {
		super(service);
	}

	private String ignored;
	private String actived;
	private String cascade;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final T obj = this.getObject(request);
		if (!Strings.isBlank(actived) || !Strings.isBlank(ignored)) {
			FieldFilter.create(service.getEntityClass(), actived, ignored).run(new Atom() {
				public void run() {
					service.dao().updateWith(obj, cascade);
				}
			});
		}
		return obj;
	}

}
