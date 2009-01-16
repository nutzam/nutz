package com.zzh.dao.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;

import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Mirror;
import com.zzh.lang.types.Castors;
import com.zzh.segment.CharSegment;
import com.zzh.segment.Segment;
import com.zzh.segment.SegmentUtils;

public class EntityField {

	private boolean pk;
	private boolean id;
	private boolean name;
	private boolean notNull;
	private boolean unsigned;
	private boolean unique;
	private boolean autoIncrement;
	private boolean index;
	private Class<?> fkClass;
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

	public boolean isPk() {
		return pk;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isId() {
		return id;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public boolean isIndex() {
		return index | id | name | isFk();
	}

	public boolean isUnique() {
		return unique;
	}

	public boolean isFk() {
		return null != fkClass;
	}

	public Class<?> getFkClass() {
		return fkClass;
	}

	void setFkClass(Class<?> fkClass) {
		this.fkClass = fkClass;
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
		return SegmentUtils.fillSegmentByKeys(defaultValue, obj).toString();
	}

	public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	public boolean isString() {
		return CharSequence.class.isAssignableFrom(field.getType());
	}

	boolean valueOf(Field field) {
		if (!evalColumnName(field))
			return false;
		pk = (field.getAnnotation(PK.class) != null);
		id = (field.getAnnotation(Id.class) != null);
		index = (field.getAnnotation(Index.class) != null);
		autoIncrement = (id ? field.getAnnotation(Id.class).value() == IdType.AUTO_INCREASE : field
				.getAnnotation(AutoIncrement.class) != null);
		name = (field.getAnnotation(Name.class) != null);
		notNull = (field.getAnnotation(NotNull.class) != null);
		unsigned = (field.getAnnotation(Unsigned.class) != null);
		unique = (field.getAnnotation(Unique.class) != null);
		FK fk = field.getAnnotation(FK.class);
		if (null != fk)
			fkClass = fk.value();
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
			throw new RuntimeException(String.format("Fail to set value [%s]->%s", obj.getClass()
					.getName(), this.getField().getName()), e);
		}
	}

}
