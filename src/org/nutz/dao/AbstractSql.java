package org.nutz.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.dao.Sql;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;

public abstract class AbstractSql<T> implements Sql<T> {

	protected AbstractSql() {
		this.values = new HashMap<String, Object>();
		this.segment = new CharSegment();
	}

	protected AbstractSql(String sql) {
		this();
		this.valueOf(sql);
	}

	protected Segment segment;
	private Entity<?> entity;
	protected Map<String, Object> values;

	public void setEntity(Entity<?> entity) {
		this.entity = entity;
	}

	public Segment getSegment() {
		return segment;
	}

	public Entity<?> getEntity() {
		return entity;
	}

	public Sql<T> setValue(Object obj) {
		// for (Field field : mirror.getFields()) {
		for (Iterator<EntityField> it = entity.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			Object value = ef.getValue(obj);
			Field field = ef.getField();
			if (null == value) {
				if (ef.hasDefaultValue()) {
					String defv = ef.getDefaultValue(obj);
					try {
						ef.setValue(obj, defv);
						values.put(field.getName(), ef.getValue(obj));
					} catch (Exception e) {
						throw Lang.wrapThrow(e);
					}
				} else if (ef.isNotNull()) {
					throw new DaoException(String.format("[%s]->'%s' can not be null", entity
							.getMirror().getType().getName(), field.getName()));
				} else
					this.set(field.getName(), null);
			} else {
				values.put(field.getName(), value);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public Sql<T> born() {
		AbstractSql<T> sql = Mirror.me(this.getClass()).born();
		sql.segment = this.segment.born();
		return sql;
	}

	public Sql<T> clone() {
		throw new RuntimeException("SQL can not be clone!!!");
	}

	public Sql<T> set(String key, boolean v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, byte v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, double v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, float v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, int v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, long v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, Object v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> set(String key, short v) {
		this.values.put(key, v);
		return this;
	}

	public Sql<T> valueOf(String s) {
		this.segment.valueOf(s);
		return this;
	}

	public String toOrginalString() {
		return this.segment.toOrginalString();
	}

	public Object get(String key) {
		return this.values.get(key);
	}

}
