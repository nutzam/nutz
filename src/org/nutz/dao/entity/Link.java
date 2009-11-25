package org.nutz.dao.entity;

import java.lang.reflect.Field;

import org.nutz.dao.TableName;
import org.nutz.dao.entity.annotation.*;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.segment.CharSegment;

public class Link {

	public Link(Mirror<?> mirror, Field field, One one) throws NoSuchFieldException {
		this.ownField = field;
		this.type = LinkType.One;
		this.targetClass = one.target();
		this.referField = mirror.getField(one.field());
		if (Mirror.me(this.referField.getType()).isStringLike()) {
			this.targetField = Mirror.me(this.targetClass).getField(Name.class);
		} else {
			this.targetField = Mirror.me(this.targetClass).getField(Id.class);
		}
	}

	public Link(Mirror<?> mirror, Field field, Many many) throws NoSuchFieldException {
		this.ownField = field;
		this.type = LinkType.Many;
		this.mapKeyField = "".equals(many.key()) ? null : many.key();
		this.targetClass = many.target();
		if (!"".equals(many.field())) {
			this.targetField = Mirror.me(this.targetClass).getField(many.field());
			if (Mirror.me(this.targetField.getType()).isStringLike()) {
				this.referField = mirror.getField(Name.class);
			} else {
				this.referField = mirror.getField(Id.class);
			}
		}
	}

	public Link(Mirror<?> mirror, Field field, ManyMany mm, boolean fromName, boolean toName) {
		this.ownField = field;
		this.type = LinkType.ManyMany;
		this.mapKeyField = "".equals(mm.key()) ? null : mm.key();
		this.targetClass = mm.target();
		this.from = mm.from();
		this.to = mm.to();
		this.relation = Relation.make(mm.relation());
		this.referField = lookupKeyField(mirror, fromName);
		this.targetField = lookupKeyField(Mirror.me(targetClass), toName);
		if (null == this.referField || null == this.targetField) {
			throw Lang.makeThrow("Fail to make ManyMany link for [%s].[%s], target: [%s]."
					+ "\n referField: [%s]" + "\n targetField: [%s]", mirror.getType().getName(),
					field.getName(), targetClass.getName(), referField, targetField);
		}

	}

	private static Field lookupKeyField(Mirror<?> mirror, boolean forName) {
		if (forName)
			for (Field f : mirror.getFields()) {
				if (null != f.getAnnotation(Name.class))
					return f;
			}
		for (Field f : mirror.getFields()) {
			if (null != f.getAnnotation(Id.class))
				return f;
		}
		return null;
	}

	// private void evalMore(String dynamicBy) {
	// Cascade cc =
	// this.targetClass.getAnnotation(Cascade.class);
	// if (null != cc && cc.value() == Cascade.TYPE.ON)
	// cascade = true;
	// Table annTab =
	// this.targetClass.getAnnotation(Table.class);
	// if (null != annTab && (new
	// CharSegment(annTab.value())).keys().size() > 0)
	// this.targetDynamic = true;
	// dynamicReferField =
	// DynamicReferPicker.eval(targetClass, dynamicBy);
	// }

	private Class<?> targetClass;
	private Field targetField;
	private Field referField;
	private Field ownField;
	private LinkType type;
	private Object relation;
	private String from;
	private String to;
	private String mapKeyField;

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public String getMapKeyField() {
		return mapKeyField;
	}

	public Field getTargetField() {
		return targetField;
	}

	public Field getReferField() {
		return referField;
	}

	public boolean isMany() {
		return type == LinkType.Many;
	}

	public boolean isOne() {
		return type == LinkType.One;
	}

	public boolean isManyMany() {
		return type == LinkType.ManyMany;
	}

	public Field getOwnField() {
		return ownField;
	}

	public static class Relation {

		static Object make(String s) {
			CharSegment cs = new CharSegment(s);
			if (cs.keys().size() == 0)
				return s;
			Relation r = new Relation();
			r.cs = cs;
			return r;
		}

		CharSegment cs;

		@Override
		public String toString() {
			return TableName.render(cs);
		}

		public String getOrginalString() {
			return cs.getOrginalString();
		}

	}

	public String getRelation() {
		return relation.toString();
	}

	public String getRelationOrignalString() {
		if (relation instanceof Relation)
			return ((Relation) relation).getOrginalString();
		return relation.toString();
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

}
