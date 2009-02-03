package com.zzh.dao.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityHolder {

	public EntityHolder() {
		mappings = new HashMap<Class<?>, Entity<?>>();
	}

	private Map<Class<?>, Entity<?>> mappings;

	/**
	 * Get one EntityMapping
	 * 
	 * @param klass
	 * @return EntityMapping class, create when it not existed
	 */
	@SuppressWarnings("unchecked")
	public <T> Entity<T> getEntity(Class<T> klass) {
		Entity<T> m = (Entity<T>) mappings.get(klass);
		if (null == m) {
			synchronized (this) {
				m = (Entity<T>) mappings.get(klass);
				if (null == m) {
					m = new Entity<T>();
					boolean parseResult = m.parse(klass);
					if (parseResult)
						mappings.put(klass, m);
					else
						m = null;
				}
			}
		}
		return m;
	}

	public int count() {
		return mappings.size();
	}
}
