package com.zzh.dao.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zzh.dao.TableNameReference;
import com.zzh.dao.entity.annotation.CONST;
import com.zzh.dao.entity.annotation.Table;
import com.zzh.dao.entity.annotation.VerifyBy;
import com.zzh.dao.entity.check.EntityChecker;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.types.Castors;
import com.zzh.segment.CharSegment;
import com.zzh.segment.Segment;
import com.zzh.segment.SegmentUtils;

public class Entity<T> {

	Entity(Castors castors) {
		this.castors = castors;
	}

	private Castors castors;
	private Map<String, EntityField> fieldMapping;
	private Mirror<T> me;
	private EntityChecker[] checkers;

	protected T born() {
		try {
			return me.born();
			// return Objects.newInstance(this.classOfT);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Fail to born [%s] becase '%s'", me
					.getMyClass().getName(), e.getMessage()), e);
		}
	}

	private Segment tableName;
	private String __stn;
	private EntityField idField;
	private EntityField[] pkFields;
	private EntityField[] autoIncrementFields;
	private EntityField[] indexFields;
	private EntityField nameField;

	void setCastors(Castors castors) {
		this.castors = castors;
	}

	public EntityField getIdField() {
		return idField;
	}

	public EntityField getNameField() {
		return nameField;
	}

	public EntityField getIdentifiedField() {
		if (hasIdField())
			return idField;
		return nameField;
	}

	public EntityField[] getIndexFields() {
		return indexFields;
	}

	public boolean hasIdField() {
		return null != idField;
	}

	public boolean hasAutoIncreaseIdField() {
		return null != idField && idField.isAutoIncrement();
	}

	public String getTableName() {
		if (null != __stn)
			return __stn;
		if (tableName.keys().size() == 0) {
			__stn = tableName.toString();
			return __stn;
		}
		Object obj = TableNameReference.get();
		if (obj == null) {
			return tableName.toString();
		}
		Segment seg = null;
		if (obj instanceof CharSequence || obj instanceof Number || obj.getClass().isPrimitive()) {
			seg = tableName.born();
			for (Iterator<String> it = seg.keys().iterator(); it.hasNext();) {
				seg.set(it.next(), obj);
			}
		} else {
			seg = tableName.born();
			SegmentUtils.fillSegmentByKeys(seg, obj);
		}
		return seg.toString();
	}

	/**
	 * Analyze one entity's setting. !!! This function must be invoked before
	 * another method.
	 * 
	 * @param classOfT
	 * @return TODO
	 */
	boolean parse(Class<T> classOfT) {
		Table table = classOfT.getAnnotation(Table.class);
		if (null == table)
			return false;
		this.me = Mirror.me(classOfT);
		// TODO think about auto add prefix and suffix
		if (CONST.NULL.equals(table.value()))
			tableName = new CharSegment(me.getMyClass().getSimpleName().toLowerCase());
		else
			tableName = new CharSegment(table.value());

		this.fieldMapping = new HashMap<String, EntityField>();
		VerifyBy vb = classOfT.getAnnotation(VerifyBy.class);
		if (null != vb) {
			checkers = new EntityChecker[vb.values().length];
			try {
				for (int i = 0; i < vb.values().length; i++) {
					checkers[i] = (EntityChecker) Class.forName(vb.values()[i]).newInstance();
				}
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		parseFields(classOfT);
		return true;

	}

	protected void parseFields(Class<?> klass) {
		Field[] fields = me.getFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				Field f = fields[i];
				EntityField ef = new EntityField();
				if (ef.valueOf(f))
					fieldMapping.put(f.getName(), ef);
			}
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		List<EntityField> pks = new LinkedList<EntityField>();
		List<EntityField> ais = new LinkedList<EntityField>();
		List<EntityField> iis = new LinkedList<EntityField>();
		for (Iterator<EntityField> it = fields().iterator(); it.hasNext();) {
			EntityField ef = it.next();
			Field f = ef.getField();
			if (ef.isId() && ef.isName())
				throw new ErrorEntitySyntaxException(klass, String.format(
						"field '%s' can not be @Id and @Name at same time", f.getName()));
			if (ef.isId()) {
				if (null != idField)
					throw new ErrorEntitySyntaxException(klass, String.format(
							"->[%s] : duplicate ID Field with [%s]", f.getName(), idField
									.getField().getName()));
				if (Number.class.isAssignableFrom(f.getType())) {
					// if (Objects.isChildOf(f.getType(), Number.class)) {
					throw new ErrorEntitySyntaxException(klass, String.format(
							"->[%s] : ID field must be a number!", f.getName()));
				}
				idField = ef;
			}
			if (ef.isName()) {
				if (null != this.nameField)
					throw new ErrorEntitySyntaxException(klass, String.format(
							"->[%s] : duplicate Name Field with [%s]", f.getName(), nameField
									.getField().getName()));
				if (!CharSequence.class.isAssignableFrom(f.getType())) {
					// if (!Objects.isChildOf(f.getType(), CharSequence.class))
					// {
					throw new ErrorEntitySyntaxException(klass, String.format(
							"->[%s] : shall be a sub-class of %s", f.getName(), CharSequence.class
									.getName()));
				}
				nameField = ef;
			}
			if (ef.isPk())
				pks.add(ef);
			if (ef.isAutoIncrement())
				ais.add(ef);
			if (ef.isIndex())
				iis.add(ef);

		}
		pkFields = pks.toArray(new EntityField[pks.size()]);
		autoIncrementFields = ais.toArray(new EntityField[pks.size()]);
		indexFields = iis.toArray(new EntityField[iis.size()]);
	}

	public Collection<EntityField> fields() {
		return fieldMapping.values();
	}

	public EntityField getField(String name) {
		return this.fieldMapping.get(name);
	}

	public Class<T> getClassOfT() {
		return me.getMyClass();
	}

	public Mirror<T> getMirror() {
		return me;
	}

	public T getObject(final ResultSet rs) {
		T obj = born();
		Iterator<EntityField> it = this.fields().iterator();
		while (it.hasNext()) {
			EntityField ef = it.next();
			ef.setValue(obj, rs, castors);
		}
		return obj;
	}

	public EntityField[] pks() {
		return this.pkFields;
	}

	public EntityField[] autoIncrements() {
		return this.autoIncrementFields;
	}
}
