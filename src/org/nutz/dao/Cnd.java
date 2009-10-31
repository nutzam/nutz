package org.nutz.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Cnd implements OrderBy, ExpGroup {

	/*------------------------------------------------------------------*/
	public static Condition format(String format, Object... args) {
		return new SimpleCondition(format, args);
	}

	public static Condition wrap(String str) {
		return new SimpleCondition((Object) str);
	}

	public static Expression exp(String name, String op, Object value) {
		return new Exp(name, op, value);
	}

	public static ExpGroup exps(String name, String op, Object value) {
		return new Cnd(Cnd.exp(name, op, value));
	}

	public static ExpGroup exps(Expression e) {
		return new Cnd(e);
	}

	public static Cnd where(String name, String op, Object value) {
		return new Cnd(Cnd.exp(name, op, value));
	}

	public static Cnd where(Expression e) {
		return new Cnd(e);
	}

	public static OrderBy orderBy() {
		return new Cnd();
	}

	/*------------------------------------------------------------------*/

	Cnd() {
		bys = new LinkedList<By>();
	}

	protected Cnd(Expression e) {
		exps = new LinkedList<Expression>();
		exps.add(e);
		bys = new LinkedList<By>();
	}

	private List<By> bys;

	public OrderBy asc(String name) {
		bys.add(new By(name, "ASC"));
		return this;
	}

	public OrderBy desc(String name) {
		bys.add(new By(name, "DESC"));
		return this;
	}

	List<Expression> exps;

	private Cnd add(String s, Expression e) {
		exps.add(new Lop(s));
		exps.add(e);
		return this;
	}

	public Cnd and(Expression e) {
		return add(" AND ", e);
	}

	public Cnd and(String name, String op, Object value) {
		return and(Cnd.exp(name, op, value));
	}

	public Cnd or(Expression e) {
		return add(" OR ", e);
	}

	public Cnd or(String name, String op, Object value) {
		return or(Cnd.exp(name, op, value));
	}

	public Cnd andNot(Expression e) {
		return add(" AND NOT ", e);
	}

	public Cnd andNot(String name, String op, Object value) {
		return andNot(Cnd.exp(name, op, value));
	}

	public Cnd orNot(Expression e) {
		return add(" OR NOT ", e);
	}

	public Cnd orNot(String name, String op, Object value) {
		return orNot(Cnd.exp(name, op, value));
	}

	public String toSql(Entity<?> entity) {
		StringBuilder sb = new StringBuilder();
		if (exps != null) {
			for (Iterator<Expression> it = exps.iterator(); it.hasNext();) {
				it.next().render(sb, entity);
			}
		}
		if (bys.size() > 0) {
			if (sb.length() > 0)
				sb.append(' ');
			sb.append("ORDER BY");
			for (Iterator<By> it = bys.iterator(); it.hasNext();) {
				it.next().render(sb, entity);
				if (it.hasNext())
					sb.append(",");
			}
		}
		return sb.toString();
	}

	public void render(StringBuilder sb, Entity<?> en) {
		sb.append('(').append(toSql(en)).append(')');
	}

	/*------------------------------------------------------------------*/
	private static class By {

		By(String name, String by) {
			this.name = name;
			this.by = by;
		}

		private String name;
		private String by;

		public void render(StringBuilder sb, Entity<?> entity) {
			EntityField ef = entity.getField(name);
			if (null == ef)
				throw Lang.makeThrow("Fail to find field '%s' in '%s'", name, entity.getType()
						.getName());
			sb.append(' ').append(ef.getColumnName()).append(' ').append(by);
		}
	}

	/*------------------------------------------------------------------*/
	private static class Lop implements Expression {
		private String s;

		private Lop(String txt) {
			this.s = txt;
		}

		public void render(StringBuilder sb, Entity<?> en) {
			sb.append(s);
		}
	}

	/*------------------------------------------------------------------*/
	private static class Exp implements Expression {

		private static final Pattern ptn = Pattern.compile("IS|LIKE|IN", Pattern.CASE_INSENSITIVE);

		Exp(String name, String op, Object value) {
			this.name = name;
			this.op = Strings.trim(op);
			if (ptn.matcher(this.op).find())
				this.op = " " + this.op + " ";
			this.value = value;
		}

		private String name;
		private String op;
		private Object value;

		public void render(StringBuilder sb, Entity<?> en) {
			if (null != en) {
				EntityField ef = en.getField(name);
				sb.append(null != ef ? ef.getColumnName() : name);
			} else
				sb.append(name);
			sb.append(op);
			sb.append(DaoUtils.formatFieldValue(value));
		}
	}

}
