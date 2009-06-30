package org.nutz.mvc.entity;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.nutz.json.Json;
import org.nutz.json.JsonException;
import org.nutz.lang.Mirror;
import org.nutz.mvc.Controllor;
import org.nutz.mvc.Params;
import org.nutz.service.EntityService;

public abstract class EntityControllor implements Controllor {

	protected EntityControllor() {
		this.format = ObjectFormat.json;
	}

	protected EntityControllor(EntityService<?> service) {
		this();
		this.service = service;
	}

	public EntityService<?> service;

	public ObjectFormat format;

	public String charset;

	protected Object getObject(HttpServletRequest request) throws JsonException, IOException {
		if (ObjectFormat.json == format)
			return Json.fromJson(service.getEntityClass(), new InputStreamReader(request
					.getInputStream(), (null == charset ? "UTF-8" : charset)));
		return Params.getObjectAsNameValuePair(Mirror.me(service.getEntityClass()).born(), request);
	}
}
