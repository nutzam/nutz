package org.nutz.mvc;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Strings;

public abstract class HttpCondition implements Condition {

	public static HttpCondition getInstance(HttpServletRequest request) {
		return new SimpleHttpCondition(request);
	}

	private static class SimpleHttpCondition extends HttpCondition {

		private SimpleHttpCondition(HttpServletRequest request) {
			super(request);
		}

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
