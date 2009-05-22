package com.zzh.mvc.entity;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import com.zzh.json.Json;
import com.zzh.json.JsonException;
import com.zzh.lang.Mirror;
import com.zzh.mvc.Controllor;
import com.zzh.mvc.Params;
import com.zzh.service.EntityService;

public abstract class EntityControllor<T> implements Controllor {

	protected EntityControllor() {
		this.format = ObjectFormat.json;
	}

	protected EntityControllor(EntityService<T> service) {
		this();
		this.service = service;
	}

	public EntityService<T> service;

	public ObjectFormat format;

	public String charset;

	protected T getObject(HttpServletRequest request) throws JsonException, IOException {
		if (ObjectFormat.json == format)
			return Json.fromJson(service.getEntityClass(), new InputStreamReader(request
					.getInputStream(), (null == charset ? "UTF-8" : charset)));
		return Params.getObjectAsNameValuePair(Mirror.me(service.getEntityClass()).born(), request);
	}
}
