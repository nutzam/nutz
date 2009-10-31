package org.nutz.dao.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.nutz.castor.Castors;
import org.nutz.dao.sql.FieldAdapter;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;

public class EntityField {

	private FieldType type;
	private boolean notNull;
	private boolean readonly;
	private Link link;
	private String columnName;
	private Segment defaultValue;
	private String _defv;
	private FieldValueType fieldType;
	private Method getter;
	private Method setter;
	private IntQuery serialQuery;
	private IntQuery nextIntQuery;
	private FieldAdapter fieldAdapter;
	private ValueAdapter valueAdapter;

	private Field field;
	private Entity<?> entity;

	public EntityField(Entity<?> entity, Field field) {
		this.entity = entity;
		this.field = field;
		// Evaluate the getter and setter
		try {
			getter = entity.mirror.getGetter(field);
			getter.setAccessible(true);
		} catch (NoSuchMethodException e) {}
		try {
			setter = entity.mirror.getSetter(field);
			setter.setAccessible(true);
		} catch (NoSuchMethodException e) {}
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setDefaultValue(Segment defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setFieldType(FieldValueType fieldType) {
		this.fieldType = fieldType;
	}

	public void setSerialQuery(IntQuery nextId) {
		this.serialQuery = nextId;
	}

	public void setNextIntQuery(IntQuery nextIntQuery) {
		this.nextIntQuery = nextIntQuery;
	}

	public void setFieldAdapter(FieldAdapter adapter) {
		this.fieldAdapter = adapter;
	}

	public void setEntity(Entity<?> entity) {
		this.entity = entity;
	}

	public void setValueAdapter(ValueAdapter valueAdapter) {
		this.valueAdapter = valueAdapter;
	}

	public Field getField() {
		return field;
	}

	public boolean isName() {
		return type == FieldType.NAME || type == FieldType.CASESENSITIVE_NAME;
	}

	public boolean isCasesensitive() {
		return type == FieldType.CASESENSITIVE_NAME;
	}

	public boolean isSerial() {
		return type == FieldType.SERIAL;
	}

	public boolean isId() {
		return type == FieldType.ID || type == FieldType.SERIAL;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public boolean isReadonly() {
		return readonly;
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
		return Segments.fillByKeys(defaultValue, obj).toString();
	}

	public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	public boolean isString() {
		return CharSequence.class.isAssignableFrom(field.getType());
	}

	public void fillValue(Object obj, ResultSet rs) {
		Object v;
		try {
			v = valueAdapter.get(rs, columnName);
		}
		/*
		 * Oracle, it object field is null, it will rise NullPointerException
		 */
		catch (Exception e1) {
			return;
		}
		if (null == v)
			return;
		try {
			this.setValue(obj, v);
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to set value [%s]->%s for the reason: '%s'", obj.getClass()
					.getName(), this.getField().getName(), e.getMessage());
		}
	}

	public Object getValue(Object obj) {
		try {
			if (null == getter)
				return this.field.get(obj);
			return getter.invoke(obj);
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to get value for object [%s]->[%s], because: '%s'",
					this.entity.mirror.getType().getName(), field.getName(), e.getMessage());
		}
	}

	public void setValue(Object obj, Object value) {
		try {
			if (null != value) {
				if (field.getType() != value)
					value = Castors.me().castTo(value, field.getType());
			}
			if (null == setter)
				this.field.set(obj, value);
			else
				setter.invoke(obj, value);
		} catch (Exception e) {
			throw Lang.makeThrow("Fail to set value for object [%s]->[%s], because: '%s'",
					this.entity.mirror.getType().getName(), field.getName(), e.getMessage());
		}
	}

	public FieldAdapter getFieldAdapter() {
		return fieldAdapter;
	}

	public Sql getSerialQuerySql() {
		return serialQuery.sql();
	}

	public Sql getNextIntQuerySql() {
		if (null == nextIntQuery)
			return null;
		return nextIntQuery.sql();
	}

	public boolean isInt() {
		return FieldValueType.INT == fieldType;
	}

	public boolean isChar() {
		return FieldValueType.CHAR == fieldType;
	}

	public boolean isAuto() {
		return FieldValueType.AUTO == fieldType;
	}

	public FieldValueType getFieldType() {
		return fieldType;
	}

	public String getFieldName() {
		return getField().getName();
	}

	public String getName() {
		return field.getName();
	}

}
