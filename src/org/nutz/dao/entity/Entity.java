package org.nutz.dao.entity;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nutz.castor.Castors;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.next.FieldQuery;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

/**
 * 描述了一个 POJO 对象同数据表之间的映射关系。
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Entity<T> {

	public Entity() {
		fields = new HashMap<String, EntityField>();
		// ones = new HashMap<String, Link>();
		// //manys = new HashMap<String, Link>();
		// manyManys = new HashMap<String, Link>();
		links = new LinkedList<Link>();
		_ln_cache = new HashMap<String, List<Link>>();
	}

	private Map<String, EntityField> fields;
	public Mirror<? extends T> mirror;

	private EntityName tableName;
	private EntityName viewName;
	private List<Link> links;
	private EntityField idField;
	private EntityField nameField;
	private EntityField[] pkFields;
	private Borning borning;
	private FieldQuery[] befores;
	private FieldQuery[] afters;

	public EntityField[] getPkFields() {
		return pkFields;
	}

	public void setPkFields(EntityField[] pkFields) {
		if (null != pkFields && pkFields.length == 1) {
			Mirror<?> mr = Mirror.me(pkFields[0].getField().getType());
			if (null == idField && mr.isIntLike()) {
				idField = pkFields[0];
			} else if (null == nameField && mr.isStringLike()) {
				nameField = pkFields[0];
			} else {
				throw Lang.makeThrow("Can not support '%s' as PK of '%s'", mr, mirror);
			}
		} else {
			this.pkFields = pkFields;
		}
	}

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
		return tableName.getOrignalString();
	}

	public String getViewName() {
		return viewName.value();
	}

	public void addField(EntityField ef) {
		fields.put(ef.getName(), ef);
		if (ef.isId())
			idField = ef;
		else if (ef.isName())
			nameField = ef;
	}

	public Collection<EntityField> fields() {
		return fields.values();
	}

	public EntityField getField(String name) {
		return this.fields.get(name);
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

	@SuppressWarnings("unchecked")
	public void setMirror(Mirror<?> mirror) {
		this.mirror = (Mirror<? extends T>) mirror;
	}

	public void setTableName(EntityName tableName) {
		this.tableName = tableName;
	}

	public void setViewName(EntityName viewName) {
		this.viewName = viewName;
	}

	public EntityName getViewNameObject() {
		return this.viewName;
	}

	public Class<? extends T> getType() {
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
			return links;
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

	public void addLinks(Link link) {
		links.add(link);
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

	public FieldQuery[] getBefores() {
		return befores;
	}

	public void setBefores(FieldQuery[] befores) {
		this.befores = befores;
	}

	public FieldQuery[] getAfters() {
		return afters;
	}

	public void setAfters(FieldQuery[] afters) {
		this.afters = afters;
	}

}
