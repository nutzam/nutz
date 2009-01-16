package com.zzh.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zzh.dao.SQLUtils;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;

public class SimpleHttpCondition implements HttpCondition {

	private Map<String, String> params;

	@SuppressWarnings("unchecked")
	@Override
	public void valueOf(HttpServletRequest request) {
		params = new HashMap<String, String>();
		for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements();) {
			String name = names.nextElement();
			params.put(name, request.getParameter(name));
		}
	}

	@Override
	public String toString(Entity<?> entity) {
		StringBuilder sb = new StringBuilder();
		for (EntityField eField : entity.getIndexFields()) {
			String v = params.get(eField.getField().getName());
			if (Strings.isBlank(v))
				continue;
			v = SQLUtils.escapteCondition(v).toString();
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append('`').append(eField.getColumnName()).append('`');
			Mirror<?> mFieldType = Mirror.me(eField.getField().getType());
			if (mFieldType.isNumber())
				sb.append('=').append(v);
			else if (eField.isFk()) {
				sb.append("='" + v + "'");
			} else
				sb.append(" LIKE '%").append(v).append("%'");
		}
		return sb.toString();
	}

}
