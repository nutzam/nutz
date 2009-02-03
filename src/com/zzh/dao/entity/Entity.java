package com.zzh.dao.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.dao.TableNameReference;
import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Mirror;
import com.zzh.lang.segment.CharSegment;
import com.zzh.lang.segment.Segment;
import com.zzh.lang.segment.Segments;

public class Entity<T> {

	Entity() {
		manyLinks = new HashMap<String, Link>();
		oneLinks = new HashMap<String, Link>();
	}

	private Map<String, EntityField> fieldMapping;
	private Mirror<T> me;

	protected T born() {
		try {
			return me.born();
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Fail to born [%s] becase '%s'", me.getMyClass().getName(),
					e.getMessage()));
		}
	}

	private Segment tableName;
	private String __stn;
	private EntityField idField;
	private Map<String, Link> manyLinks;
	private Map<String, Link> oneLinks;
	private EntityField nameField;

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
		if (obj instanceof CharSequence || obj instanceof Number
				|| obj.getClass().isPrimitive()) {
			seg = tableName.born();
			for (Iterator<String> it = seg.keys().iterator(); it.hasNext();) {
				seg.set(it.next(), obj);
			}
		} else {
			seg = tableName.born();
			Segments.fillSegmentByKeys(seg, obj);
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
		if (CONST.NULL.equals(table.value()))
			tableName = new CharSegment(me.getMyClass().getSimpleName()
					.toLowerCase());
		else
			tableName = new CharSegment(table.value());

		this.fieldMapping = new HashMap<String, EntityField>();
		/* parse all children fields */
		for (Field f : me.getFields()) {
			EntityField ef = new EntityField();
			Object re = ef.valueOf(me, f);
			if (re instanceof Boolean) {
				fieldMapping.put(f.getName(), ef);
				if (ef.isId() && ef.isName())
					throw new ErrorEntitySyntaxException(
							classOfT,
							String
									.format(
											"field '%s' can not be @Id and @Name at same time",
											f.getName()));
				if (ef.isId()) {
					if (null != idField)
						throw new ErrorEntitySyntaxException(
								classOfT,
								String
										.format(
												"->[%s] : duplicate ID Field with [%s]",
												f.getName(), idField.getField()
														.getName()));
					if (Number.class.isAssignableFrom(f.getType())) {
						throw new ErrorEntitySyntaxException(classOfT, String
								.format("->[%s] : ID field must be a number!",
										f.getName()));
					}
					idField = ef;
				}
				if (ef.isName()) {
					if (null != this.nameField)
						throw new ErrorEntitySyntaxException(
								classOfT,
								String
										.format(
												"->[%s] : duplicate Name Field with [%s]",
												f.getName(), nameField
														.getField().getName()));
					if (!CharSequence.class.isAssignableFrom(f.getType())) {
						throw new ErrorEntitySyntaxException(classOfT, String
								.format("->[%s] : shall be a sub-class of %s",
										f.getName(), CharSequence.class
												.getName()));
					}
					nameField = ef;
				}
			} else if (re instanceof Link) {
				if (((Link) re).isMany())
					this.manyLinks.put(((Link) re).getOwnField().getName(),
							((Link) re));
				else
					this.oneLinks.put(((Link) re).getOwnField().getName(),
							((Link) re));
			}
		}
		/* done for parse all children fields */
		return true;
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

	public T getObject(final ResultSet rs, Castors castors) {
		T obj = born();
		Iterator<EntityField> it = this.fields().iterator();
		while (it.hasNext()) {
			EntityField ef = it.next();
			ef.setValue(obj, rs, castors);
		}
		return obj;
	}

	public boolean hasLinkToOne() {
		return this.oneLinks.size() > 0;
	}

	public boolean hasLinkToMany() {
		return this.manyLinks.size() > 0;
	}

	public Map<String, Link> getManyLinks() {
		return manyLinks;
	}

	public Map<String, Link> getOneLinks() {
		return oneLinks;
	}

}
