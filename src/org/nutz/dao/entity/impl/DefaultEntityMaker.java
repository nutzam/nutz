package org.nutz.dao.entity.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.EntityName;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.entity.born.Borns;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;

/**
 * This class must be drop after make() be dropped 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 *
 */
public class DefaultEntityMaker implements EntityMaker {
	
	private Entity entity;

	public  Entity make(DatabaseMeta db, Class<?> type) {
		entity = new Entity();
		Mirror<?> mirror = Mirror.me(type);
		entity.setMirror(mirror);
		
		// Get @Table & @View
		entity.setTableName(evalEntityName(type, Table.class, null));
		entity.setViewName(evalEntityName(type, View.class, Table.class));
		
		// Borning
		entity.setBorning(Borns.evalBorning(entity));
		
		// For each fields
		for (Field f : mirror.getFields()) {
			
		}
		
		return entity;
	}
	
	private EntityField evalField()

	private EntityName evalEntityName(	Class<?> type,
										Class<? extends Annotation> annType,
										Class<? extends Annotation> dftAnnType) {
		Annotation ann = null;
		Class<?> me = type;
		while (null != me && !(me == Object.class)) {
			ann = me.getAnnotation(annType);
			if (ann != null) {
				String v = Mirror.me(annType).invoke(ann, "value").toString();
				if (!Strings.isBlank(v))
					return EntityName.create(v);
			}
			me = me.getSuperclass();
		}
		if (null != dftAnnType)
			return evalEntityName(type, dftAnnType, null);
		return EntityName.create(type.getSimpleName().toLowerCase());
	}
}
