package org.nutz.dao.entity;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.DatabaseMeta;
import org.nutz.lang.Lang;

public class EntityHolder {

	public EntityHolder(Class<? extends EntityMaker> maker) {
		this.maker = maker;
		mappings = new HashMap<Class<?>, Entity<?>>();
	}

	private Class<? extends EntityMaker> maker;
	private Map<Class<?>, Entity<?>> mappings;

	@SuppressWarnings("unchecked")
	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return (Entity<T>) mappings.get(classOfT);
	}

	@SuppressWarnings("unchecked")
	public <T> Entity<T> reloadEntity(Class<T> classOfT, Connection conn, DatabaseMeta meta) {
		Entity<?> entity = null;
		try {
			entity = maker.newInstance().make(meta, conn, classOfT);
			mappings.put(classOfT, entity);
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		return (Entity<T>) entity;
	}

	public int count() {
		return mappings.size();
	}
}
