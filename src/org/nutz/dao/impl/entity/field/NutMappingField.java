package org.nutz.dao.impl.entity.field;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.entity.EntityObjectContext;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Lang;
import org.nutz.lang.segment.Segment;

public class NutMappingField extends AbstractEntityField implements MappingField {

	private String columnName;

	private ColType columnType;

	private Segment defaultValue;

	private int width;

	private int precision;

	private boolean isCompositePk;

	private boolean isId;

	private boolean isName;

	private boolean readonly;

	private boolean notNull;

	private boolean unsigned;

	private boolean autoIncreasement;

	private boolean casesensitive;

	private ValueAdaptor adaptor;

	public NutMappingField(Entity<?> entity) {
		super(entity);
		casesensitive = true;
	}

	public ValueAdaptor getAdaptor() {
		return adaptor;
	}

	public void setAdaptor(ValueAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public void injectValue(Object obj, ResultSet rs) {
		try {
			this.setValue(obj, adaptor.get(rs, columnName));
		}
		catch (SQLException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public String getColumnName() {
		return columnName;
	}

	public ColType getColumnType() {
		return columnType;
	}

	public String getDefaultValue(Object obj) {
		if (null == defaultValue)
			return null;
		if (null == obj)
			defaultValue.toString();
		return defaultValue.render(new EntityObjectContext(getEntity(), obj)).toString();
	}

	public int getWidth() {
		return width;
	}

	public int getPrecision() {
		return precision;
	}

	public boolean isCompositePk() {
		return isCompositePk;
	}

	public boolean isPk() {
		return isId || (!isId && isName) || isCompositePk;
	}

	public boolean isId() {
		return isId;
	}

	public boolean isName() {
		return isName;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public boolean isCasesensitive() {
		return casesensitive;
	}

	public boolean isAutoIncreasement() {
		return autoIncreasement;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setColumnType(ColType columnType) {
		this.columnType = columnType;
	}

	public void setDefaultValue(Segment defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setAsCompositePk() {
		this.isCompositePk = true;
	}

	public void setAsId() {
		this.isId = true;
	}

	public void setAsName() {
		this.isName = true;
	}

	public void setAsReadonly() {
		this.readonly = true;
	}

	public void setAsNotNull() {
		this.notNull = true;
	}

	public void setAsUnsigned() {
		this.unsigned = true;
	}

	public void setAsCasesensitive() {
		this.casesensitive = true;
	}

	public void setAsAutoIncreasement() {
		this.autoIncreasement = true;
	}

}
