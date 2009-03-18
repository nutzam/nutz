package com.zzh.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.dao.Sql;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.segment.CharSegment;
import com.zzh.lang.segment.Segment;

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

	@Override
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
			} else if (Mirror.me(field.getType()).isBoolean()) {
				values.put(field.getName(), value);
			} else {
				values.put(field.getName(), value);
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Sql<T> born() {
		AbstractSql<T> sql = Mirror.me(this.getClass()).born();
		sql.segment = this.segment.born();
		return sql;
	}

	@Override
	public Sql<T> clone() {
		throw new RuntimeException("SQL can not be clone!!!");
	}

	@Override
	public Sql<T> set(String key, boolean v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, byte v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, double v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, float v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, int v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, long v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, Object v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> set(String key, short v) {
		this.values.put(key, v);
		return this;
	}

	@Override
	public Sql<T> valueOf(String s) {
		this.segment.valueOf(s);
		return this;
	}

	@Override
	public String toString() {
		return this.segment.setAll('?').toString();
	}

	@Override
	public String toOrginalString() {
		return this.segment.toOrginalString();
	}

	@Override
	public Object get(String key) {
		return this.values.get(key);
	}

}
