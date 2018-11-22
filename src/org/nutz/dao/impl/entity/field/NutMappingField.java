package org.nutz.dao.impl.entity.field;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.Record;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.entity.EntityObjectContext;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.segment.Segment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NutMappingField extends AbstractEntityField implements MappingField {

	private String columnName;
	
	private String columnNameInSql;

	private ColType columnType;

	private Segment defaultValue;

	private String columnComment;

	private int width;

	private int precision;

	private boolean isCompositePk;

	private boolean isId;

	private boolean isName;

	private boolean isVersion;

	private boolean readonly;

	private boolean notNull;

	private boolean unsigned;

	private boolean autoIncreasement;

	private boolean casesensitive;

	private boolean hasColumnComment;

	private String customDbType;

	private ValueAdaptor adaptor;

	private boolean insert = true;

	private boolean update = true;
	
	private static final Log log = Logs.get();

	public NutMappingField(Entity<?> entity) {
		super(entity);
		casesensitive = true;
	}

	@Override
    public ValueAdaptor getAdaptor() {
		return adaptor;
	}

	@Override
    public void setAdaptor(ValueAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	@Override
    public void injectValue(Object obj, Record rec, String prefix) {
		try {
			Object val = rec.get(prefix == null ? columnName : prefix + columnName);
			this.setValue(obj, val);
		}
		catch (Exception e) {
            if (log.isTraceEnabled()) {
                log.tracef("columnName="+columnName, e);
            }
		}
	}

	@Override
    public void injectValue(Object obj, ResultSet rs, String prefix) {
		try {
			this.setValue(obj, adaptor.get(rs, prefix == null ? columnName : prefix + columnName));
		}
		catch (SQLException e) {
		    if (log.isTraceEnabled()) {
		        log.tracef("columnName="+columnName, e);
		    }
		}
	}

	@Override
    public String getColumnName() {
		return columnName;
	}

	@Override
    public ColType getColumnType() {
		return columnType;
	}

	@Override
    public String getDefaultValue(Object obj) {
		if (null == defaultValue) {
            return null;
        }
		String re;
		if (null == obj || defaultValue.keyCount() == 0) {
            re = defaultValue.toString();
        } else {
            re = defaultValue.render(new EntityObjectContext(getEntity(), obj)).toString();
        }
		return re;
	}

	@Override
    public int getWidth() {
		return width;
	}

	@Override
    public int getPrecision() {
		return precision;
	}

	@Override
    public boolean isCompositePk() {
		return isCompositePk;
	}

	@Override
    public boolean isPk() {
		return isId || (!isId && isName) || isCompositePk;
	}

	@Override
    public boolean isId() {
		return isId;
	}

	@Override
    public boolean isName() {
		return isName;
	}

	@Override
    public boolean isReadonly() {
		return readonly;
	}

	@Override
    public boolean hasDefaultValue() {
		return null != defaultValue;
	}

	@Override
    public boolean isNotNull() {
		return notNull;
	}

	@Override
    public boolean isCasesensitive() {
		return casesensitive;
	}

	@Override
    public boolean isAutoIncreasement() {
		return autoIncreasement;
	}

	@Override
    public boolean isUnsigned() {
		return unsigned;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Override
    public void setColumnType(ColType columnType) {
		this.columnType = columnType;
	}

	public void setColumnComment(String columnComment) {
		this.columnComment = columnComment;
	}

	public void setHasColumnComment(boolean hasColumnComment) {
		this.hasColumnComment = hasColumnComment;
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

	@Override
    public void setAsReadonly() {
		this.readonly = true;
	}

	@Override
    public void setAsNotNull() {
		this.notNull = true;
	}

	public void setAsUnsigned() {
		this.unsigned = true;
	}

	public void setCasesensitive(boolean casesensitive) {
		this.casesensitive = casesensitive;
	}

	public void setAsAutoIncreasement() {
		this.autoIncreasement = true;
	}
	
	public void setAutoIncreasement(boolean autoIncreasement) {
        this.autoIncreasement = autoIncreasement;
    }

	@Override
    public String getColumnComment() {
		return columnComment;
	}

	@Override
    public boolean hasColumnComment() {
		return hasColumnComment;
	}

	@Override
    public void setCustomDbType(String customDbType) {
		this.customDbType = customDbType;
	}

	@Override
    public String getCustomDbType() {
		return customDbType;
	}

	@Override
    public boolean isInsert() {
		return insert;
	}

	@Override
    public boolean isUpdate() {
		return update;
	}

	public void setInsert(boolean insert) {
		this.insert = insert;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	@Override
    public String getColumnNameInSql() {
	    if (columnNameInSql != null) {
            return columnNameInSql;
        }
	    return columnName;
	}
	
	public void setColumnNameInSql(String columnNameInSql) {
        this.columnNameInSql = columnNameInSql;
    }

	@Override
    public boolean isVersion() {
		return isVersion;
	}

	public void setAsVersion() {
		this.isVersion = true;
	}
}
