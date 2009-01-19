package com.zzh.dao.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.lang.types.Castors;

public class EntityHolder {

	public EntityHolder(Castors castors) {
		mappings = new HashMap<Class<?>, Entity<?>>();
		this.castors = null == castors ? Castors.me() : castors;
	}

	private Castors castors;

	private Map<Class<?>, Entity<?>> mappings;

	public void changeCastors(Castors castors) {
		this.castors = castors;
		for (Iterator<Entity<?>> it = mappings.values().iterator(); it.hasNext();) {
			it.next().setCastors(castors);
		}
	}

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
					m = new Entity<T>(castors);
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
