package com.zzh.mvc;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import com.zzh.dao.Condition;
import com.zzh.dao.Sqls;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Strings;

public abstract class HttpCondition implements Condition {

	public static HttpCondition getInstance(HttpServletRequest request) {
		return new SimpleHttpCondition(request);
	}

	private static class SimpleHttpCondition extends HttpCondition {

		private SimpleHttpCondition(HttpServletRequest request) {
			super(request);
		}

		@Override
		public String toString(Entity<?> entity) {
			StringBuilder sb = new StringBuilder();
			for (Enumeration<?> en = request.getParameterNames(); en.hasMoreElements();) {
				String name = en.nextElement().toString();
				EntityField ef = entity.getField(name);
				if (null == ef)
					continue;
				String value = request.getParameter(name);
				if (null == value) {
					sb.append(String.format("%s IS NULL", ef.getColumnName()));
				} else if (Sqls.isNotNeedQuote(ef.getField().getType())) {
					sb.append(String.format("%s=%s", ef.getColumnName(), value));
				} else {
					sb.append(String.format("%s='%s'", ef.getColumnName(), value));
				}
				sb.append(" AND ");
			}
			String re = sb.toString();
			if (re.endsWith(" AND "))
				re = re.substring(0, re.length() - 5);
			if (!Strings.isBlank(orderBy))
				return re + this.orderBy;
			return re;
		}
	}

	protected HttpServletRequest request;
	protected String orderBy;

	protected HttpCondition(HttpServletRequest request) {
		this.request = request;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = Strings.trim(orderBy);
		if (!Strings.isBlank(this.orderBy) && !this.orderBy.startsWith("ORDER BY")) {
			this.orderBy = "ORDER BY " + this.orderBy;
		}
	}

}
