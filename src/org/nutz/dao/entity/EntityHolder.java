package org.nutz.dao.entity;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.DatabaseMeta;

public class EntityHolder {

	public EntityHolder(EntityMaker maker) {
		this.maker = maker;
		mappings = new HashMap<Class<?>, Entity<?>>();
	}

	private EntityMaker maker;
	private Map<Class<?>, Entity<?>> mappings;

	@SuppressWarnings("unchecked")
	public <T> Entity<T> getEntity(Class<T> classOfT) {
		return (Entity<T>) mappings.get(classOfT);
	}

	@SuppressWarnings("unchecked")
	public <T> Entity<T> reloadEntity(Class<T> classOfT, Connection conn, DatabaseMeta meta) {
		Entity<?> entity = null;
		entity = maker.make(meta, conn, classOfT);
		mappings.put(classOfT, entity);
		return (Entity<T>) entity;
	}

	public int count() {
		return mappings.size();
	}
}
