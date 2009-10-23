package org.nutz.dao.entity;

import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.DatabaseMeta;
import org.nutz.lang.Lang;

public class EntityHolder {

	public EntityHolder(Class<? extends EntityMaker> maker) {
		this.maker = maker;
		mappings = new HashMap<Class<?>, Entity>();
	}

	private Class<? extends EntityMaker> maker;
	private Map<Class<?>, Entity> mappings;

	/**
	 * Get one EntityMapping
	 * 
	 * @param klass
	 * @return EntityMapping class, create when it not existed
	 */
	public Entity getEntity(Class<?> klass, DatabaseMeta meta) {
		Entity entity = (Entity) mappings.get(klass);
		if (null == entity)
			synchronized (this) {
				entity = (Entity) mappings.get(klass);
				if (null == entity) {
					try {
						entity = maker.newInstance().make(meta, klass);
						mappings.put(klass, entity);
					} catch (Exception e) {
						throw Lang.wrapThrow(e);
					}
				}
			}
		return entity;
	}

	public int count() {
		return mappings.size();
	}
}
