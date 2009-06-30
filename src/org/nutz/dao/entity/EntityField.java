package org.nutz.dao.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;

import org.nutz.castor.Castors;
import org.nutz.dao.Database;
import org.nutz.dao.Sql;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;

public class EntityField {

	private Id id;
	private Name name;
	private boolean notNull;
	private boolean readonly;
	private Link link;
	private String columnName;
	private Segment defaultValue;
	private String _defv;
	private Type.ENUM type;
	private Method getter;
	private Method setter;
	private Next nextId;

	private Field field;
	private Entity<?> entity;

	EntityField(Entity<?> entity) {
		this.entity = entity;
	}

	public Field getField() {
		return field;
	}

	public boolean isName() {
		return name != null;
	}

	public boolean isCaseInsensitive() {
		return isName() && !name.casesensitive();
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
		return Segments.fillSegmentByKeys(defaultValue, obj).toString();
	}

	public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	public boolean isString() {
		return CharSequence.class.isAssignableFrom(field.getType());
	}

	Object valueOf(Database db, EntityName entityName, Mirror<?> mirror, Field field) {
		/*
		 * Evaluate @One|@Many|@ManyMany
		 */
		link = Link.eval(mirror, field);
		if (null != link)
			return link;
		if (!evalColumnName(field))
			return null;
		// Save field object
		this.field = field;
		this.field.setAccessible(true);
		/*
		 * Init current field getter/setter to speeded up the reflection
		 */
		try {
			getter = entity.mirror.getGetter(field);
			getter.setAccessible(true);
		} catch (NoSuchMethodException e) {}
		try {
			setter = entity.mirror.getSetter(field);
			setter.setAccessible(true);
		} catch (NoSuchMethodException e) {}
		/*
		 * Load misc attributes
		 */
		readonly = (field.getAnnotation(Readonly.class) != null);
		Type t = field.getAnnotation(Type.class);
		type = null == t ? Type.ENUM.AUTO : t.value();
		/*
		 * Check default value
		 */
		evalDefaultValue(field);
		/*
		 * Check @Id
		 */
		id = field.getAnnotation(Id.class);
		// If auto-increasment, require a setter or public
		// field
		if (isAutoIncrement()) {
			if (!field.isAccessible() && null == setter)
				Lang.makeThrow(
						"Entity field [%s]->[%s], so must be accessible or has a setter, %s",
						entity.mirror.getType().getName(), field.getName(),
						"for the reason it is auto-increament @Id.");
		}
		/*
		 * Evaluate the fetch next Id SQL
		 */
		if (isId())
			nextId = Next.create(db, entityName, id.next(), columnName);
		/*
		 * Check @Name
		 */
		name = field.getAnnotation(Name.class);
		if (isName()) {
			if (!Mirror.me(field.getType()).isStringLike())
				Lang.makeThrow("Entity field [%s]->[%s] is @Name, so it must be a String.",
						entity.mirror.getType().getName(), field.getName());
			notNull = true;
		} else {
			notNull = (field.getAnnotation(NotNull.class) != null);
		}
		/*
		 * Finish parsing, return true. If current field is a Link (Return Link
		 * object) or not @Column (return null) at the begining of this method.
		 */
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
		if (Lang.NULL.equals(column.value()))
			columnName = field.getName();
		else
			columnName = column.value();
		return true;
	}

	void fillFieldValueFromResultSet(Object obj, ResultSet rs) {
		try {
			Object v = rs.getObject(columnName);
			if (null == v)
				return;
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

	public Sql<Integer> getFetchSql() {
		return nextId.sql();
	}

	public boolean isInt() {
		return Type.ENUM.INT == type;
	}

	public boolean isChar() {
		return Type.ENUM.CHAR == type;
	}

	public boolean isAuto() {
		return Type.ENUM.AUTO == type;
	}
}
