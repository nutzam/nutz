package com.zzh.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zzh.castor.Castors;
import com.zzh.json.Json;
import com.zzh.lang.Lang;
import com.zzh.lang.Localize;
import com.zzh.lang.Mirror;
import com.zzh.mvc.annotation.Param;
import com.zzh.mvc.annotation.Parameter;

public class Params {

	public static <T> Parameter[] getParameterFields(Class<?> type) {
		Field[] fields = Mirror.me(type).getFields();
		ArrayList<Parameter> list = new ArrayList<Parameter>(fields.length);
		for (Field f : fields) {
			Param ann = f.getAnnotation(Param.class);
			if (ann != null) {
				if (ann.value().equals(Lang.NULL)) {
					list.add(new Parameter(f.getName(), f));
				} else {
					list.add(new Parameter(ann.value(), f));
				}
			}
		}
		if(list.size()==0){
			for (Field f : fields) {
				list.add(new Parameter(f.getName(), f));
			}
		}
		return list.toArray(new Parameter[list.size()]);
	}

	public static <T> T getObjectAsNameValuePair(T obj, HttpServletRequest request) {
		return Params.getObjectAsNameValuePair(obj, request, getParameterFields(obj.getClass()));
	}

	public static <T> T getObjectAsNameValuePair(T obj, HttpServletRequest request,
			Parameter[] fields) {
		if (null == obj)
			return null;
		Mirror<? extends Object> me = Mirror.me(obj.getClass());
		try {
			for (Parameter f : fields) {
				String v = request.getParameter(f.getName());
				if (null == v)
					continue;
				v = Localize.convertAscii2Native(v, '%');
				Object v2 = Castors.me().castTo(v, f.getField().getType());
				me.setValue(obj, f.getField(), v2);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return obj;
	}

	public static <T> T getObjectAsJson(Class<T> classOfT, HttpServletRequest request,
			String charset) throws UnsupportedEncodingException, IOException {
		return Json.fromJson(classOfT, new BufferedReader(new InputStreamReader(request
				.getInputStream(), charset)));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMapAsJson(HttpServletRequest request, String charset)
			throws UnsupportedEncodingException, IOException {
		return (Map<String, Object>) Json.fromJson(new BufferedReader(new InputStreamReader(request
				.getInputStream(), charset)));
	}

	public static Map<String, Object> getMapAsJson(HttpServletRequest request) throws IOException {
		return getMapAsJson(request, "UTF-8");
	}

}
