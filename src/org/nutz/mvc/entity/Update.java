package org.nutz.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.FieldFilter;
import org.nutz.lang.Strings;
import org.nutz.service.EntityService;
import org.nutz.trans.Atom;

public class Update extends EntityControllor {

	protected Update() {
		super();
	}

	public Update(EntityService<?> service) {
		super(service);
	}

	private String ignored;
	private String actived;
	private String cascade;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final Object obj = this.getObject(request);
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
