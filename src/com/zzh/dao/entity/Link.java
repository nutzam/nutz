package com.zzh.dao.entity;

import java.lang.reflect.Field;

import com.zzh.dao.entity.annotation.*;
import com.zzh.lang.Mirror;

public class Link {

	static Link eval(Mirror<?> mrr, Field field) {
		try {
			Link link = new Link();
			link.ownField = field;
			One one = field.getAnnotation(One.class);
			if (null != one) { // One > refer own field
				link.many = false;
				link.targetClass = one.target();
				link.referField = mrr.getField(one.field());
				if (Mirror.me(link.referField.getType()).isStringLike()) {
					link.targetField = Mirror.me(link.targetClass).getField(Name.class);
				} else {
					link.targetField = Mirror.me(link.targetClass).getField(Id.class);
				}
				return link;
			} else { // Many > refer target field
				Many many = field.getAnnotation(Many.class);
				if (null != many) {
					link.many = true;
					link.targetClass = many.target();
					link.targetField = Mirror.me(link.targetClass).getField(many.field());
					if (Mirror.me(link.targetField.getType()).isStringLike()) {
						link.referField = mrr.getField(Name.class);
					} else {
						link.referField = mrr.getField(Id.class);
					}
					return link;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("Fail to eval linked field '%s' because '%s'",
					field.getName(), e.getMessage()));
		}
		return null;
	}

	private Class<?> targetClass;
	private Field targetField;
	private Field referField;
	private Field ownField;
	private boolean many;

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Field getTargetField() {
		return targetField;
	}

	public Field getReferField() {
		return referField;
	}

	public boolean isMany() {
		return many;
	}

	public Field getOwnField() {
		return ownField;
	}

}
