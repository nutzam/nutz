package org.nutz.dao.entity;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.castor.Castors;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.annotation.*;
import org.nutz.dao.entity.born.Borns;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class Entity {

	public Entity() {
		fieldMapping = new HashMap<String, EntityField>();
		ones = new HashMap<String, Link>();
		manys = new HashMap<String, Link>();
		manyManys = new HashMap<String, Link>();
		links = new LinkedList<Link>();
		_ln_cache = new HashMap<String, List<Link>>();
	}

	private Map<String, EntityField> fieldMapping;
	public Mirror<?> mirror;

	private EntityName tableName;
	private EntityName viewName;
	private EntityField idField;
	private Map<String, Link> manys;
	private Map<String, Link> ones;
	private Map<String, Link> manyManys;
	private List<Link> links;
	private EntityField nameField;
	private Borning borning;

	public EntityField getIdField() {
		return idField;
	}

	public EntityField getNameField() {
		return nameField;
	}

	public EntityField getIdentifiedField() {
		if (null != idField)
			return idField;
		return nameField;
	}

	public String getTableName() {
		return tableName.value();
	}

	public String getOrignalTableName() {
		return tableName.orignalString();
	}

	public String getViewName() {
		return viewName.value();
	}

	/**
	 * Analyze one entity's setting. !!! This function must be invoked before
	 * another method.
	 * 
	 * @param classOfT
	 * @return TODO
	 */
	public boolean parse(Class<?> classOfT, DatabaseMeta db) {
		this.mirror = Mirror.me(classOfT);
		Table table = evalTable(classOfT);
		if (null == table)
			return false;
		// eval table name
		if (Lang.NULL.equals(table.value()))
			tableName = EntityName.create(mirror.getType().getSimpleName().toLowerCase());
		else
			tableName = EntityName.create(table.value());
		// eval view name
		View view = classOfT.getAnnotation(View.class);
		if (null == view) {
			viewName = tableName;
		} else {
			viewName = EntityName.create(view.value());
		}

		// evalu fields
		Borns.evalBorning(this);

		/* parse all children fields */
		for (Field f : mirror.getFields()) {
			EntityField ef = new EntityField(this);
			Object re = ef.valueOf(db, tableName, mirror, f);
			if (re instanceof Boolean) {
				fieldMapping.put(f.getName(), ef);
				if (ef.isId() && ef.isName())
					throw new ErrorEntitySyntaxException(classOfT, String.format(
							"field '%s' can not be @Id and @Name at same time", f.getName()));
				if (ef.isId()) {
					if (null != idField)
						throw new ErrorEntitySyntaxException(classOfT, String.format(
								"->[%s] : duplicate ID Field with [%s]", f.getName(), idField
										.getField().getName()));
					if (Number.class.isAssignableFrom(f.getType())) {
						throw new ErrorEntitySyntaxException(classOfT, String.format(
								"->[%s] : ID field must be a number!", f.getName()));
					}
					idField = ef;
				}
				if (ef.isName()) {
					if (null != this.nameField)
						throw new ErrorEntitySyntaxException(classOfT, String.format(
								"->[%s] : duplicate Name Field with [%s]", f.getName(), nameField
										.getField().getName()));
					if (!CharSequence.class.isAssignableFrom(f.getType())) {
						throw new ErrorEntitySyntaxException(classOfT, String.format(
								"->[%s] : shall be a sub-class of %s", f.getName(),
								CharSequence.class.getName()));
					}
					nameField = ef;
				}
			} else if (re instanceof Link) {
				if (((Link) re).isMany())
					this.manys.put(((Link) re).getOwnField().getName(), ((Link) re));
				else if (((Link) re).isManyMany())
					this.manyManys.put(((Link) re).getOwnField().getName(), ((Link) re));
				else
					this.ones.put(((Link) re).getOwnField().getName(), ((Link) re));
				this.links.add((Link) re);
			}
		}
		/* done for parse all children fields */
		return true;
	}

	public void addField(EntityField ef){
		fieldMapping.put(ef.getName(), ef);
	}
	
	private Table evalTable(Class<?> type) {
		Table table = null;
		Class<?> theClass = type;
		while (null != theClass && !(theClass == Object.class)) {
			table = theClass.getAnnotation(Table.class);
			if (table != null)
				return table;
			theClass = theClass.getSuperclass();
		}
		return table;
	}

	public Collection<EntityField> fields() {
		return fieldMapping.values();
	}

	public EntityField getField(String name) {
		return this.fieldMapping.get(name);
	}

	public Borning getBorning() {
		return borning;
	}

	public void setBorning(Borning borning) {
		this.borning = borning;
	}

	public Mirror<?> getMirror() {
		return mirror;
	}

	public void setMirror(Mirror<?> mirror) {
		this.mirror = mirror;
	}

	public void setTableName(EntityName tableName) {
		this.tableName = tableName;
	}

	public void setViewName(EntityName viewName) {
		this.viewName = viewName;
	}

	public Class<?> getType() {
		return mirror.getType();
	}

	public Object getObject(final ResultSet rs, FieldMatcher actived) {
		try {
			return borning.born(rs, actived);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	private Map<String, List<Link>> _ln_cache;

	public List<Link> getLinks(String regex) {
		if (null == regex)
			return null;
		List<Link> re = _ln_cache.get(regex);
		if (null == re) {
			synchronized (this) {
				re = _ln_cache.get(regex);
				if (null == re) {
					re = new LinkedList<Link>();
					Pattern p = Pattern.compile(regex);
					for (Iterator<Link> it = links.iterator(); it.hasNext();) {
						Link e = it.next();
						if (p.matcher(e.getOwnField().getName()).find())
							re.add(e);
					}
				}
				_ln_cache.put(regex, re);
			}
		}
		return re;
	}

	public Map<String, Link> getManys() {
		return manys;
	}

	public Map<String, Link> getOnes() {
		return ones;
	}

	public Map<String, Link> getManyManys() {
		return manyManys;
	}

	public long getId(Object obj) {
		if (null == idField)
			return 0;
		return Castors.me().castTo(idField.getValue(obj), long.class);
	}

	public String getName(Object obj) {
		if (null == nameField)
			return null;
		return Castors.me().castToString(nameField.getValue(obj));
	}

}
