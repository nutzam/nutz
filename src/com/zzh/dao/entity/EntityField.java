package com.zzh.dao.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;

import com.zzh.castor.Castors;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Mirror;
import com.zzh.lang.segment.CharSegment;
import com.zzh.lang.segment.Segment;
import com.zzh.lang.segment.Segments;

public class EntityField {

	private Id id;
	private boolean name;
	private boolean notNull;
	private Link link;
	private String columnName;
	private Segment defaultValue;
	private String _defv;

	private Field field;

	public Field getField() {
		return field;
	}

	public boolean isName() {
		return name;
	}

	public boolean isAutoIncrement() {
		return null != id && IdType.AUTO_INCREASE == id.value();
	}

	public boolean isId() {
		return null != id;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public Link getLink() {
		return link;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getDefaultValue(Object obj) {
		if (null != _defv)
			return _defv;
		if (defaultValue.keys().size() == 0) {
			_defv = defaultValue.render().toString();
			return _defv;
		}
		if (null == obj)
			return defaultValue.render().toString();
		return Segments.fillSegmentByKeys(defaultValue, obj).toString();
	}

	public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	public boolean isString() {
		return CharSequence.class.isAssignableFrom(field.getType());
	}

	Object valueOf(Mirror<?> mirror, Field field) {
		link = Link.eval(mirror, field);
		if (null != link)
			return link;
		if (!evalColumnName(field))
			return null;
		id = field.getAnnotation(Id.class);
		name = (field.getAnnotation(Name.class) != null);
		notNull = (field.getAnnotation(NotNull.class) != null);
		evalDefaultValue(field);
		this.field = field;
		return true;
	}

	private void evalDefaultValue(Field field) {
		Default def = field.getAnnotation(Default.class);
		if (null != def)
			defaultValue = new CharSegment(def.value());
		else
			defaultValue = null;
	}

	private boolean evalColumnName(Field field) {
		if (Modifier.isTransient(field.getModifiers()))
			return false;
		Column column = field.getAnnotation(Column.class);
		if (null == column)
			return false;
		if (CONST.NULL.equals(column.value()))
			columnName = field.getName();
		else
			columnName = column.value();
		return true;
	}

	void setValue(Object obj, ResultSet rs, Castors castors) {
		try {
			Object v = rs.getObject(columnName);
			if (null == v)
				return;
			Object cv = castors.castTo(v, getField().getType());
			Mirror.me(obj.getClass()).setValue(obj, getField(), cv);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Fail to set value [%s]->%s", obj.getClass().getName(),
					this.getField().getName()), e);
		}
	}

}
