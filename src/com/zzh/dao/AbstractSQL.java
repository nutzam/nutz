package com.zzh.dao;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.zzh.dao.FailToMakeSQLException;
import com.zzh.dao.SQL;
import com.zzh.dao.SQLUtils;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.EntityField;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.types.Castors;
import com.zzh.segment.CharSegment;
import com.zzh.segment.SegmentUtils;

public abstract class AbstractSQL<T> extends CharSegment implements SQL<T> {

	private Entity<?> entity;

	public Entity<?> getEntityMapping() {
		return entity;
	}

	public void setEntityMapping(Entity<?> entityMapping) {
		this.entity = entityMapping;
	}

	@Override
	public SQL<T> setValue(Object obj) {
		SegmentUtils.fillSegmentByKeys(this, obj);
		for (Iterator<EntityField> it = entity.fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			Field f = ef.getField();
			Object v = entity.getMirror().getValue(obj, f);
			if (null == v) {
				if (ef.hasDefaultValue()) {
					String defv = ef.getDefaultValue(obj);
					if (SQLUtils.isNotNeedQuote(f.getType()))
						this.set(f.getName(), defv);
					else
						this.set(f.getName(), String
								.format("'%s'", SQLUtils.escapeFieldValue(defv)));
					try {
						Mirror.me(obj.getClass()).setValue(obj, f, defv);
					} catch (Exception e) {
						throw Lang.wrapThrow(e);
					}
				} else if (ef.isNotNull())
					throw new FailToMakeSQLException(String.format("[%s]->'%s' can not be null",
							entity.getClassOfT().getName(), f.getName()));
				else
					this.set(f.getName(), "NULL");
			} else {
				String vs = Castors.me().castToString(v, f.getType());
				if (SQLUtils.isNotNeedQuote(f.getType()))
					this.set(f.getName(), vs);
				else
					this.set(f.getName(), String.format("'%s'", SQLUtils.escapeFieldValue(vs)));
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SQL<T> born() {
		return (SQL<T>) super.born();
	}

	@SuppressWarnings("unchecked")
	@Override
	public SQL<T> clone() {
		return (SQL) super.clone();
	}

}
