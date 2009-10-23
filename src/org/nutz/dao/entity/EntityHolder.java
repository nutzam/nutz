package org.nutz.dao.entity;

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

	/**
	 * Get one EntityMapping
	 * 
	 * @param klass
	 * @return EntityMapping class, create when it not existed
	 */
	@SuppressWarnings("unchecked")
	public <T> Entity<T> getEntity(Class<T> klass, DatabaseMeta meta) {
		Entity<?> entity = mappings.get(klass);
		if (null == entity)
			synchronized (this) {
				entity = mappings.get(klass);
				if (null == entity) {
					try {
						entity = maker.newInstance().make(meta, klass);
						mappings.put(klass, entity);
					} catch (Exception e) {
						throw Lang.wrapThrow(e);
					}
				}
			}
		return (Entity<T>) entity;
	}

	public int count() {
		return mappings.size();
	}
}
